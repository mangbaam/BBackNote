package mangbaam.bbacknote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentCreateNoteBinding
import mangbaam.bbacknote.model.NoteEntity

class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)

        binding.chkLock.setOnCheckedChangeListener { _, checked ->
            if(checked) binding.etPassword.visibility = View.VISIBLE
            else binding.etPassword.visibility = View.INVISIBLE
        }

        binding.btnSave.setOnClickListener {
            if (binding.noteContent.text.isNullOrBlank().not()) {
                if (binding.chkLock.isChecked && binding.etPassword.text.isNullOrBlank()) {
                    floatInputPasswordDialog()
                } else {
                    val note = NoteEntity(
                        binding.noteContent.text.toString().trim(),
                        binding.chkLock.isChecked,
                        R.drawable.rectangle_corner8_white
                    )
                    val bundle = bundleOf("newNote" to note)
                    // TODO 노트 ROOM에 저장
                    binding.root.findNavController().navigate(R.id.action_createNoteFragment_to_noteListFragment, bundle)
                }
            } else {
                Toast.makeText(it.context, "작성된 내용이 없어서 저장하지 않습니다", Toast.LENGTH_LONG).show()
                it.findNavController().popBackStack()
            }
        }

        return binding.root
    }

    private fun floatInputPasswordDialog() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}