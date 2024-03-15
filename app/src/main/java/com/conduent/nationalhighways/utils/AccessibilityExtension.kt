package com.conduent.nationalhighways.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import com.conduent.nationalhighways.utils.common.Utils
import com.google.android.material.checkbox.MaterialCheckBox

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
            } else if(!isChecked && eventType==1){
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

fun announceStateChange(checkBox: MaterialCheckBox, isChecked: Boolean, context: Context) {
    val announcement =
        if (isChecked) "on" else "off"

    checkBox.contentDescription = announcement
    checkBox.announceForAccessibility(announcement)
}
