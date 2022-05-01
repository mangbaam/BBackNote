package mangbaam.bbacknote.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentEditNoteBinding
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.util.DialogBuilder
import mangbaam.bbacknote.util.onTextLength

class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private var toast: Toast? = null
    private var isLocked: Boolean = false
    private val note by lazy { requireArguments().getSerializable("note") as NoteEntity }
    private val dialogBuilder: DialogBuilder by lazy { DialogBuilder(requireContext()) }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_note, container, false)

        setNoteData()

        val lock = binding.lottieLock

        lock.setOnClickListener {
            when (isLocked) {
                true -> {
                    val animator = ValueAnimator.ofFloat(0.9F, 0F).setDuration(500)
                    animator.addUpdateListener { lock.progress = it.animatedValue as Float }
                    animator.start()
                    isLocked = false
                }
                false -> {
                    val currentPassword = MyApplication.encryptedPrefs.getPassword()
                    if (currentPassword.isNullOrBlank()) {
                        dialogBuilder.requireInitPasswordDialog { result ->
                            lockNoteIfPasswordSuccess(result, lock)
                        }
                    } else {
                        dialogBuilder.requirePasswordDialog { result ->
                            lockNoteIfPasswordSuccess(result, lock)
                        }
                    }
                }
            }
        }

        binding.noteContent.onTextLength {
            binding.tvContentLength.text = it.toString()
        }

        binding.btnUpdate.setOnClickListener {
            if (binding.noteContent.text.isNullOrBlank().not()) {
                val updatedNote = NoteEntity(
                    binding.etTitle.text.toString().trim(),
                    binding.noteContent.text.toString().trim(),
                    isLocked,
                    R.drawable.rectangle_corner8_white
                )
                if (note.title == updatedNote.title &&
                    note.secret == updatedNote.secret &&
                    note.content == updatedNote.content &&
                    note.noteColor == updatedNote.noteColor
                ) {
                    makeToast("변경된 내용이 없습니다")
                    findNavController().popBackStack()
                } else {
                    note.title = updatedNote.title
                    note.secret = updatedNote.secret
                    note.content = updatedNote.content
                    note.noteColor = updatedNote.noteColor

                    viewModel.updateNote(note)
                    it.findNavController().popBackStack()
                }

            } else {
                makeToast("작성된 내용이 없어서 기존 내용이 변경되지 않습니다")
                it.findNavController().popBackStack()
            }
        }

        return binding.root
    }

    private fun setNoteData() {
        binding.run {
            etTitle.setText(note.title)
            isLocked = note.secret
            noteContent.setText(note.content)
            tvContentLength.text = note.content.length.toString()
            lottieLock.progress = if (note.secret) 0.9F else 0F
        }
    }

    private fun lockNoteIfPasswordSuccess(success: Boolean, lock: LottieAnimationView) {
        if (success) {
            val animator = ValueAnimator.ofFloat(0F, 0.9F).setDuration(500)
            animator.addUpdateListener {
                lock.progress = it.animatedValue as Float
            }
            animator.start()
            isLocked = true
        }
    }

    private fun makeToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}