package com.conduent.nationalhighways.utils

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.clearCheckedRadioButtonsContentDescriptions
import com.conduent.nationalhighways.utils.widgets.NHTextView

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
