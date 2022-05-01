package mangbaam.bbacknote.util

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import mangbaam.bbacknote.MyApplication
import mangbaam.bbacknote.R

class DialogBuilder(private val context: Context) {
    private var toast: Toast? = null

    fun requireInitPasswordDialog(result: ((Boolean) -> Unit)) {
        AlertDialog.Builder(context, R.style.CustomDialog)
            .setView(R.layout.dialog_first_password)
            .show()
            .also { dialog ->
                if (dialog == null) return@also

                val passwordEditText = dialog.findViewById<TextInputEditText>(R.id.et_password)
                val completeButton = dialog.findViewById<MaterialButton>(R.id.btn_complete)
                val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

                passwordEditText?.setFocusAndShowKeyboard(context)

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

    fun requirePasswordDialog(result: ((Boolean) -> Unit)) {
        AlertDialog.Builder(context, R.style.CustomDialog)
            .setView(R.layout.dialog_require_password)
            .show()
            .also { dialog ->
                if (dialog == null) return@also

                val passwordEditText = dialog.findViewById<TextInputEditText>(R.id.et_password)
                val completeButton = dialog.findViewById<MaterialButton>(R.id.btn_complete)
                val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

                passwordEditText?.setFocusAndShowKeyboard(context)

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

    fun changeUserPasswordDialog() {
        AlertDialog.Builder(context, R.style.CustomDialog)
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

                currentPasswordEditText?.setFocusAndShowKeyboard(context)

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

    fun deleteAllNotes(result: (Boolean) -> Unit) {
        AlertDialog.Builder(context)
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
                requirePasswordDialog {
                    if (it) result(true)
                }
            }
            .create()
            .show()
    }

    private fun makeToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }
}