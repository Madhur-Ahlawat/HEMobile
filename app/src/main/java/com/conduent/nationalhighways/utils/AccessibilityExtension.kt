package com.conduent.nationalhighways.utils

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import com.conduent.apollo.ui.CMDropDownView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.widgets.NHRadioButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText

fun RadioButton.setAccessibilityDelegate() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfo
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (isChecked) {
                info.text = text
            }
        }

        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            } else if (!isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            }
        }
    }
}


fun SwitchCompat.setAccessibilityDelegate() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            } else if (!isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            }
        }
    }
}


fun AppCompatCheckBox.setAccessibilityDelegate() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            } else if (!isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            }
        }
    }
}

fun NHRadioButton.setAccessibilityDelegate() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            } else if (!isChecked && eventType == 1) {
                announceForAccessibility(Utils.replaceAsterisks(text.toString()))
            }
        }
    }
}

fun announceStateChange(checkBox: MaterialCheckBox, isChecked: Boolean, context: Context) {
    val announcement =
        if (isChecked) "on" else "off"

    checkBox.contentDescription = announcement
    checkBox.announceForAccessibility(announcement)
}

fun setPersonalInfoAnnouncement(view: View, context: Context) {
    view.isClickable = false
    view.contentDescription = context.getString(R.string.acc_pi_data)
}


fun TextInputEditText.setAccessibilityDelegateForDigits() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            Log.e("TAG", "sendAccessibilityEvent: eventType " + eventType)

        }

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            Log.e("TAG", "sendAccessibilityEvent: eventType = " + hint)

            if (info.isFocusable == true) {
                info.text = Utils.accessibilityForNumbers(text.toString())
                info.hintText = hint.toString()
            }
        }
    }
}

fun EditText.setAccessibilityDelegateForDigits() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            Log.e("TAG", "sendAccessibilityEvent: eventType " + eventType)

        }

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            Log.e("TAG", "sendAccessibilityEvent: eventType =")

            if (info.isFocusable == true) {
                info.text = Utils.accessibilityForNumbers(text.toString())
            }
        }
    }
}

fun CMDropDownView.clickActionForDropdown(){
    editText.setOnTouchListener { v, event ->

        if (event.action == MotionEvent.ACTION_UP) {
            val drawableRight = editText.compoundDrawables[2] // index 2 for right drawable
            drawableRight?.let {
                if (event.rawX >= (editText.right - editText.compoundPaddingRight)) {
                    // Perform your action here
                    showPopUp()
                    return@setOnTouchListener true
                }
            }
        }
        return@setOnTouchListener false
    }
}