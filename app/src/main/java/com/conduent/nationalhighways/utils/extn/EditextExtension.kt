package com.conduent.nationalhighways.utils.extn

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.addExpriryListner() {
    this.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.length == 2) {
                if(start==2 && before==1 && !s.toString().contains("/")){
                    setText("${s.toString()[0]}")
                    setSelection(1)
                }
                else {
                    setText("$s/")
                    setSelection(3)
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

    })
}