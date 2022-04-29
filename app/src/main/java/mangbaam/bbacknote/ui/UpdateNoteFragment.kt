package mangbaam.bbacknote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentEditNoteBinding
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.util.onTextLength
import mangbaam.bbacknote.util.setFocusAndShowKeyboard

class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private var toast: Toast? = null
    private var isLockable: Boolean = false
    private val note by lazy { requireArguments().getSerializable("note") as NoteEntity }

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

        binding.chkLock.setOnCheckedChangeListener { checkBox, checked ->
            lifecycleScope.launch(Dispatchers.Main) {
                if (checked) {
                    if (isLockable.not()) {
                        requirePasswordDialog { success ->
                            checkBox.isChecked = success
                            isLockable = success
                        }
                    }
                } else {
                    if (isLockable) isLockable = false
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
                    isLockable,
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
            isLockable = note.secret
            chkLock.isChecked = note.secret
            noteContent.setText(note.content)
            tvContentLength.text = note.content.length.toString()
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
                    result(false)
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
        result(false)
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