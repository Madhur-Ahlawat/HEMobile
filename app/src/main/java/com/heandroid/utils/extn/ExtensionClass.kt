package com.heandroid.utils.extn

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.text.SpannableString
import android.text.style.UnderlineSpan


// method to hide keyboard in activity
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

// method to hide keyboard in fragment
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.showToast(s: String?) {
    s?.let {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}


fun TextView.changeTextColor(id: Int) {
    setTextColor(ContextCompat.getColor(this.context, id))
}

fun TextView.changeBackgroundColor(id: Int) {
    setBackgroundColor(ContextCompat.getColor(this.context, id))
}


fun View.visible() {
    visibility = View.VISIBLE
}


fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

// text underline
fun TextView.setUnderLineTxt(str: String) {
    val content = SpannableString(str)
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
    text = content

}

fun TextView.setStyleNormal() {
    setTypeface(null, Typeface.NORMAL)
}


fun TextView.setStyleBold() {
    setTypeface(typeface, Typeface.BOLD)
}
