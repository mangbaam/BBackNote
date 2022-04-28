package mangbaam.bbacknote.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import mangbaam.bbacknote.R
import mangbaam.bbacknote.adapter.NoteListAdapter
import mangbaam.bbacknote.databinding.FragmentNoteListBinding
import mangbaam.bbacknote.util.Contants.TAG
import mangbaam.bbacknote.util.RecyclerViewUtil

class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelFactory(requireContext()))[MainViewModel::class.java]
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

        listAdapter = NoteListAdapter { note ->
            Toast.makeText(requireContext(), "${note.id} 클릭됨", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}