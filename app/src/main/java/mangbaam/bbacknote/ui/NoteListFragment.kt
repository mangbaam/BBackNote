package mangbaam.bbacknote.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import mangbaam.bbacknote.MainActivity
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.adapter.NoteListAdapter
import mangbaam.bbacknote.databinding.FragmentNoteListBinding
import mangbaam.bbacknote.util.BackKeyHandler
import mangbaam.bbacknote.util.Contants.TAG
import mangbaam.bbacknote.util.RecyclerViewUtil
import mangbaam.bbacknote.util.setFocusAndShowKeyboard

class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private var toast: Toast? = null
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[MainViewModel::class.java]
    }
    private lateinit var listAdapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_list, container, false)
        binding.viewmodel = viewModel

        viewModel.getAllNotes()

        setToolbar()
        initRecyclerView()
        onOptionItemsSelectListener()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteListFragment_to_createNoteFragment)
        }

        return binding.root
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbarNoteList)
        setHasOptionsMenu(true)
    }

    private fun onOptionItemsSelectListener() {
        binding.toolbarNoteList.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.change_user_password -> {
                    if (MyApplication.encryptedPrefs.getPassword()
                            .isNullOrBlank()
                    ) floatInitPasswordDialog {}
                    else changeUserPassword()
                }
                R.id.lock_all_notes -> lockAllNotes()
                R.id.unlock_all_notes -> unlockAllNotes()
                R.id.delete_all_notes -> deleteAllNotes()
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_note_list, menu)
        val activity = requireActivity() as MainActivity
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    viewModel.getAllNotes()
                } else {
                    viewModel.search(query)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                return true
            }
        }

        searchView = (menu.findItem(R.id.search_note).actionView as SearchView).apply {
            maxWidth = Integer.MAX_VALUE
            queryHint = "노트 제목, 내용으로 검색합니다"
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setOnQueryTextListener(queryTextListener)
        }
    }

    private fun initRecyclerView() {
        listAdapter = NoteListAdapter { note, item ->
            when (item) {
                NoteListAdapter.ClickedItem.CONTENT -> {
                    if (note.secret) {
                        requirePasswordDialog { success ->
                            if (success) {
                                findNavController().navigate(
                                    R.id.action_noteListFragment_to_updateNoteFragment,
                                    bundleOf("note" to note)
                                )
                            }
                        }
                    } else findNavController().navigate(
                        R.id.action_noteListFragment_to_updateNoteFragment,
                        bundleOf("note" to note)
                    )
                }
                NoteListAdapter.ClickedItem.LOCK_NOTE -> {
                    val currentPassword = MyApplication.encryptedPrefs.getPassword()
                    if (currentPassword.isNullOrBlank()) {
                        floatInitPasswordDialog { success ->
                            if (success) {
                                requirePasswordDialog { result ->
                                    if (result) {
                                        viewModel.lockNote(note)
                                    }
                                }
                            }
                        }
                    } else {
                        requirePasswordDialog { result ->
                            if (result) {
                                viewModel.lockNote(note)
                            }
                        }
                    }
                }
                NoteListAdapter.ClickedItem.DELETE_NOTE -> {
                    if (note.secret) {
                        requirePasswordDialog { result ->
                            if (result) viewModel.deleteNote(note)
                        }
                    } else {
                        viewModel.deleteNote(note)
                    }
                }
                NoteListAdapter.ClickedItem.UNLOCK_NOTE -> {
                    val currentPassword = MyApplication.encryptedPrefs.getPassword()
                    if (currentPassword.isNullOrBlank()) {
                        floatInitPasswordDialog { success ->
                            if (success) {
                                requirePasswordDialog { result ->
                                    if (result) {
                                        viewModel.unlockNote(note)
                                    }
                                }
                            }
                        }
                    } else {
                        requirePasswordDialog { result ->
                            if (result) {
                                viewModel.unlockNote(note)
                            }
                        }
                    }
                }
            }
        }

        binding.rvNoteList.apply {
            adapter = listAdapter
            val countOfColumns = RecyclerViewUtil.calculateCountOfColumns(
                this.context,
                marginWidthDp = 40F,
                columnWidthDp = 200F
            )
            layoutManager =
                GridLayoutManager(this.context, countOfColumns, LinearLayoutManager.VERTICAL, false)
        }
        viewModel.noteList.observe(viewLifecycleOwner) { noteList ->
            Log.d(TAG, "NoteListFragment - submitList: $noteList")
            listAdapter.submitList(noteList)
            binding.lottieFire.visibility = if (noteList.isEmpty()) View.VISIBLE else View.GONE
            binding.tvEmptyNoteDescription.visibility = if (noteList.isEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun deleteAllNotes() {
        AlertDialog.Builder(requireContext())
            .setTitle("모든 노트가 삭제됩니다")
            .setMessage("삭제된 메시지는 복구할 수 없습니다. 그래도 삭제하시겠습니까?")
            .setIcon(R.drawable.ic_warning_hotpink)
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .setNeutralButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("전체 삭제") { dialog, _ ->
                dialog.dismiss()
                requirePasswordDialog { result ->
                    if (result) viewModel.deleteAllNotes()
                }
            }
            .create()
            .show()
    }

    private fun unlockAllNotes() {
        requirePasswordDialog { result ->
            if (!result) return@requirePasswordDialog
            viewModel.unlockAllNotes()
        }
    }

    private fun lockAllNotes() {
        requirePasswordDialog { result ->
            if (!result) return@requirePasswordDialog
            viewModel.lockAllNotes()
        }
    }

    private fun changeUserPassword() {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_change_password)
            .show()
            .also { dialog ->
                if (dialog == null) return@also

                val currentPasswordEditText =
                    dialog.findViewById<TextInputEditText>(R.id.et_current_password)
                val newPasswordEditText =
                    dialog.findViewById<TextInputEditText>(R.id.et_new_password)
                val completeButton = dialog.findViewById<MaterialButton>(R.id.btn_complete)
                val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

                currentPasswordEditText?.setFocusAndShowKeyboard(requireContext())

                cancelButton?.setOnClickListener {
                    dialog.dismiss()
                }
                completeButton?.setOnClickListener {
                    val password = currentPasswordEditText?.text?.trim().toString()
                    val currentPassword = MyApplication.encryptedPrefs.getPassword()

                    val correspond = password == currentPassword
                    if (!correspond) {
                        makeToast("현재 비밀번호가 일치하지 않습니다")
                    } else {
                        if (newPasswordEditText?.text?.toString().isNullOrBlank()) {
                            makeToast("비밀번호에 공백이 올 수 없습니다")
                        } else {
                            MyApplication.encryptedPrefs.setPassword(
                                newPasswordEditText?.text?.trim().toString()
                            )
                            makeToast("비밀번호가 성공적으로 변경되었습니다")
                            dialog.dismiss()
                        }
                    }
                }
            }
    }

    private fun requirePasswordDialog(result: ((Boolean) -> Unit)) {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_require_password)
            .show()
            .also { dialog ->
                if (dialog == null) return@also

                val passwordEditText = dialog.findViewById<TextInputEditText>(R.id.et_password)
                val completeButton = dialog.findViewById<MaterialButton>(R.id.btn_complete)
                val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

                passwordEditText?.setFocusAndShowKeyboard(requireContext())

                cancelButton?.setOnClickListener {
                    dialog.dismiss()
                }
                completeButton?.setOnClickListener {
                    val password = passwordEditText?.text?.trim().toString()
                    val userPassword = MyApplication.encryptedPrefs.getPassword()
                    val same = password == userPassword
                    result(same)
                    if (!same) {
                        makeToast("비밀번호가 일치하지 않습니다")
                    }
                    dialog.dismiss()
                }
            }
    }

    private fun floatInitPasswordDialog(result: ((Boolean) -> Unit)) {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_first_password)
            .show()
            .also { dialog ->
                if (dialog == null) return@also

                val passwordEditText = dialog.findViewById<TextInputEditText>(R.id.et_password)
                val completeButton = dialog.findViewById<MaterialButton>(R.id.btn_complete)
                val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

                passwordEditText?.setFocusAndShowKeyboard(requireContext())

                cancelButton?.setOnClickListener {
                    dialog.dismiss()
                    result(false)
                }
                completeButton?.setOnClickListener {
                    val password = passwordEditText?.text?.trim()
                    when {
                        password.isNullOrBlank() -> {
                            passwordEditText?.error = "비밀번호를 다시 입력하세요"
                            result(false)
                        }
                        password.contains(" ") -> {
                            passwordEditText.error = "비밀번호에 공백이 올 수 없습니다"
                            result(false)
                        }
                        else -> {
                            MyApplication.encryptedPrefs.setPassword(password.toString())
                            dialog.dismiss()
                            result(true)
                        }
                    }
                }
            }
    }

    private fun makeToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (searchView.isIconified.not()) searchView.isIconified = true
                    else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toast = null
        _binding = null
    }
}