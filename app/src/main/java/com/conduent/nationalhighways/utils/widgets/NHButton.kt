package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.util.AttributeSet
import com.conduent.apollo.ui.CMButton

/**
 * Created by Mohammed Sameer Ahmad .
 */
const val TAG = "ButtonTest"
open class NHButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CMButton(context, attrs){
//    override fun setEnabled(enabled: Boolean) {
//        alpha = when {
//            enabled -> {
//                1.0f
//            }
//            else -> {
//                this.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonColorPrimaryDisable))
//                0.5f
//            }
//        }
//        super.setEnabled(enabled)
//    }
}
