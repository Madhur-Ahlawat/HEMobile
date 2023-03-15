package com.conduent.nationalhighways.utils.extn

import android.text.SpannableString
import android.text.style.UnderlineSpan


/**
 * Created by Mohammed Sameer Ahmad .
 */
fun String.underline(){
    val content = SpannableString(this)
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
}