package com.heandroid.utils.extn

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.material.button.MaterialButton


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

fun View.inVisible() {
    visibility = View.INVISIBLE
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

/**
 * To open the key board.
 */
fun EditText.openKeyboardForced() {
    requestFocus()
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        0
    )
}

/**
 * To open the key board.
 */
fun EditText.openKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
        this, InputMethodManager.SHOW_IMPLICIT
    )
}

fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

/**
 * To open the key board.
 */
fun EditText.hideKeyboard() {
    val inputMethodManager =
        this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

/**
 * to highlight and underline some part of text and clickable
 */
fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1

    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Selection.setSelection((widget as TextView).text as Spannable, 0)
                widget.invalidate()
                link.second.onClick(widget)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FF3700B3")
                ds.isUnderlineText = true
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan,
            startIndexOfLink,
            startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

    }
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}