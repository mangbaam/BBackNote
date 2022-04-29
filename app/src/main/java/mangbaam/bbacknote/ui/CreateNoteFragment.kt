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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentCreateNoteBinding
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.util.onTextLength
import mangbaam.bbacknote.util.setFocusAndShowKeyboard

class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!
    private var toast: Toast? = null
    private var isLockable: Boolean = false

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)

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

        binding.btnSave.setOnClickListener {
            if (binding.noteContent.text.isNullOrBlank().not()) {
                val note = NoteEntity(
                    binding.etTitle.text.toString().trim(),
                    binding.noteContent.text.toString().trim(),
                    isLockable,
                    R.drawable.rectangle_corner8_white
                )
                viewModel.addNote(note)
                it.findNavController().popBackStack()

            } else {
                makeToast("작성된 내용이 없어서 저장하지 않습니다")
                it.findNavController().popBackStack()
            }
        }

        return binding.root
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