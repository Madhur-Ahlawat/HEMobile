package com.conduent.nationalhighways.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.onTextChanged(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener(s.toString())
        }
    })
}
fun addChar(str: String, ch: Char, position: Int): String {
    val len = str.length
    val updatedArr = CharArray(len + 1)
    str.toCharArray(updatedArr, 0, 0, position)
    updatedArr[position] = ch
    str.toCharArray(updatedArr, position + 1, position, len)
    return String(updatedArr)
}