package com.conduent.nationalhighways.utils

import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
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
                val infoCompat = AccessibilityNodeInfoCompat.wrap(info)

                infoCompat.roleDescription = ""
//                infoCompat.isEditable = false
            }
        }

        override fun sendAccessibilityEvent(host: View, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            Log.e("TAG", "sendAccessibilityEvent: eventType "+eventType )
            if (eventType == 1) {
                announceForAccessibility("Selected: amrutha")
            }
        }

        override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) {
            super.sendAccessibilityEventUnchecked(host, event)
            Log.e("TAG", "sendAccessibilityEventUnchecked: eventType " )
        }
    }
}




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
            }
        }
    }
}

