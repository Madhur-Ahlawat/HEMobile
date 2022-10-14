package com.conduent.nationalhighways.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.conduent.nationalhighways.R

class CustomTextView : AppCompatTextView {


    constructor(context: Context) : super(context) {
        initResources(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initResources(context, attrs)

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initResources(context, attrs)

    }

    private fun initResources(context: Context, attrs: AttributeSet?) {

        val mTypeFace = ResourcesCompat.getFont(context, R.font.roboto_medium)
        typeface = mTypeFace
    }


}