package mangbaam.bbacknote.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentNoteListBinding

class NoteListFragment : Fragment() {

    private lateinit var viewModel: NoteListViewModel
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_list, container, false)
        viewModel = ViewModelProvider(requireActivity())[NoteListViewModel::class.java]
        binding.viewmodel = viewModel

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_noteListFragment_to_createNoteFragment)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}