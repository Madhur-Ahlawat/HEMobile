package com.conduent.nationalhighways.utils

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.conduent.apollo.ui.CMDropDownView
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.clearCheckedRadioButtonsContentDescriptions
import com.conduent.nationalhighways.utils.widgets.NHTextView
import com.google.android.material.checkbox.MaterialCheckBox

fun RadioButton.setupAccessibilityDelegate() {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (info.isAccessibilityFocused) {
                // Set content description for the RadioGroup
                val radioGroup = parent as? RadioGroup
                radioGroup?.clearCheckedRadioButtonsContentDescriptions()
                radioGroup?.contentDescription = Utils.replaceAsterisks(text.toString())
            }
        }
    }
}
fun NHTextView.setupTextAccessibilityDelegate(textview: NHTextView, text : String){
    val accessibilityDelegate: View.AccessibilityDelegate =
        object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfo
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = text
            }
        }
    textview.accessibilityDelegate = accessibilityDelegate
}
fun announceStateChange(checkBox: MaterialCheckBox, isChecked: Boolean, context: Context) {
    val announcement =
        if (isChecked) "on" else "off"

    checkBox.contentDescription = announcement
    checkBox.announceForAccessibility(announcement)
}
