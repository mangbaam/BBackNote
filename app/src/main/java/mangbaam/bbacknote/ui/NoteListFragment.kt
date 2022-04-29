package mangbaam.bbacknote.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.adapter.NoteListAdapter
import mangbaam.bbacknote.databinding.FragmentNoteListBinding
import mangbaam.bbacknote.util.Contants.TAG
import mangbaam.bbacknote.util.RecyclerViewUtil

class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[MainViewModel::class.java]
    }
    private lateinit var listAdapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_list, container, false)
        binding.viewmodel = viewModel

        viewModel.getAllNotes()

        initRecyclerView()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteListFragment_to_createNoteFragment)
        }

        return binding.root
    }

    private fun initRecyclerView() {

        listAdapter = NoteListAdapter { note, item ->
            when (item) {
                NoteListAdapter.ClickedItem.CONTENT -> {
                    Toast.makeText(requireContext(), "$note 컨텐츠 확인", Toast.LENGTH_SHORT).show()
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

                cancelButton?.setOnClickListener {
                    dialog.dismiss()
                }
                completeButton?.setOnClickListener {
                    val password = passwordEditText?.text?.trim().toString()
                    val userPassword = MyApplication.encryptedPrefs.getPassword()
                    val same = password == userPassword
                    result(same)
                    if (!same) {
                        Toast.makeText(it.context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}