package mangbaam.bbacknote.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText

fun EditText.setFocusAndShowKeyboard(context: Context) {
    this.requestFocus()
    this.text?.length?.let { setSelection(it) }
    this.postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }, 100)
}

fun EditText.clearFocusAndHideKeyboard(context: Context) {
    this.clearFocus()
    this.postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }, 30)
}

fun TextInputEditText.setFocusAndShowKeyboard(context: Context) {
    this.requestFocus()
    this.text?.length?.let { setSelection(it) }
    this.postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 100)
}

fun TextInputEditText.clearFocusAndHideKeyboard(context: Context) {
    this.clearFocus()
    this.postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }, 30)
}

fun EditText.onTextLength(length: (Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val str = p0 ?: ""
            length(str.length)
        }

        override fun afterTextChanged(p0: Editable?) {}
    })
}