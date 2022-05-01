package mangbaam.bbacknote.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import mangbaam.bbacknote.MainActivity
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.adapter.NoteListAdapter
import mangbaam.bbacknote.databinding.FragmentNoteListBinding
import mangbaam.bbacknote.util.Contants.TAG
import mangbaam.bbacknote.util.DialogBuilder
import mangbaam.bbacknote.util.RecyclerViewUtil

class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private val dialogBuilder: DialogBuilder by lazy {DialogBuilder(requireContext())}
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
                    if (MyApplication.encryptedPrefs.getPassword().isNullOrBlank())
                        dialogBuilder.requireInitPasswordDialog {}
                    else dialogBuilder.changeUserPasswordDialog()
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
                        dialogBuilder.requirePasswordDialog { success ->
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
                        dialogBuilder.requireInitPasswordDialog { success ->
                            if (success) {
                                dialogBuilder.requirePasswordDialog { result ->
                                    if (result) {
                                        viewModel.lockNote(note)
                                    }
                                }
                            }
                        }
                    } else {
                        dialogBuilder.requirePasswordDialog { result ->
                            if (result) {
                                viewModel.lockNote(note)
                            }
                        }
                    }
                }
                NoteListAdapter.ClickedItem.DELETE_NOTE -> {
                    if (note.secret) {
                        dialogBuilder.requirePasswordDialog { result ->
                            if (result) viewModel.deleteNote(note)
                        }
                    } else {
                        viewModel.deleteNote(note)
                    }
                }
                NoteListAdapter.ClickedItem.UNLOCK_NOTE -> {
                    val currentPassword = MyApplication.encryptedPrefs.getPassword()
                    if (currentPassword.isNullOrBlank()) {
                        dialogBuilder.requireInitPasswordDialog { success ->
                            if (success) {
                                dialogBuilder.requirePasswordDialog { result ->
                                    if (result) {
                                        viewModel.unlockNote(note)
                                    }
                                }
                            }
                        }
                    } else {
                        dialogBuilder.requirePasswordDialog { result ->
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
        dialogBuilder.deleteAllNotes { result ->
            if (result) viewModel.deleteAllNotes()
        }
    }

    private fun unlockAllNotes() {
        dialogBuilder.requirePasswordDialog { result ->
            if (!result) return@requirePasswordDialog
            viewModel.unlockAllNotes()
        }
    }

    private fun lockAllNotes() {
        dialogBuilder.requirePasswordDialog { result ->
            if (!result) return@requirePasswordDialog
            viewModel.lockAllNotes()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().apply {
            onBackPressedDispatcher.addCallback(
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
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}