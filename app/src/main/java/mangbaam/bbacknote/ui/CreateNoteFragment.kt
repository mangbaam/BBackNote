package mangbaam.bbacknote.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.FragmentCreateNoteBinding
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.util.DialogBuilder
import mangbaam.bbacknote.util.onTextLength
import mangbaam.bbacknote.util.setFocusAndShowKeyboard

class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!
    private var toast: Toast? = null
    private var isLocked: Boolean = false
    private val dialogBuilder by lazy { DialogBuilder(requireContext()) }

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
                        dialogBuilder.requireInitPasswordDialog { success ->
                            lockNoteIfPasswordSuccess(success, lock)
                        }
                    } else {
                        dialogBuilder.requirePasswordDialog { success ->
                            lockNoteIfPasswordSuccess(success, lock)
                        }
                    }
                }
            }
        }

        binding.lottieLock.progress = 0F

        binding.noteContent.onTextLength {
            binding.tvContentLength.text = it.toString()
        }

        binding.btnSave.setOnClickListener {
            if (binding.noteContent.text.isNullOrBlank().not()) {
                val note = NoteEntity(
                    binding.etTitle.text.toString().trim(),
                    binding.noteContent.text.toString().trim(),
                    isLocked,
                    R.drawable.rectangle_corner8_white
                )
                viewModel.addNote(note)
                it.findNavController().popBackStack()

            } else {
                makeToast("????????? ????????? ????????? ???????????? ????????????")
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
                        makeToast("??????????????? ???????????? ????????????")
                    }
                    dialog.dismiss()
                }
            }
        result(false)
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
                            passwordEditText?.error = "??????????????? ?????? ???????????????"
                            result(false)
                        }
                        password.contains(" ") -> {
                            passwordEditText.error = "??????????????? ????????? ??? ??? ????????????"
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