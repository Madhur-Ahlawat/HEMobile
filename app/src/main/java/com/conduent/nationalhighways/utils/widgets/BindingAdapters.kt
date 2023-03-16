package com.conduent.nationalhighways.utils.widgets

import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter





/**
 * Created by Mohammed Sameer Ahmad .
 */
@BindingAdapter("app:title")
fun setTitleToNHInputCell(edittext: AppCompatEditText, str: String?) {
    if (str == null) {
        edittext.setText("")
    } else {
        edittext.setText(str)
    }
}