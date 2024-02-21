package com.conduent.nationalhighways.ui.customviews

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.widget.AppCompatTextView
import android.graphics.Paint
import android.util.AttributeSet

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.conduent.nationalhighways.R

class CustomTabView: AppCompatTextView {

    private var showDividerVal:Boolean=false


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

        val mTypeFace = ResourcesCompat.getFont(context, com.conduent.nationalhighways.R.font.roboto_medium)
        typeface = mTypeFace
    }

    fun setDivider(show:Boolean)
    {
        showDividerVal = show
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(showDividerVal)
        {
            val paint = Paint()
            paint.color = ContextCompat.getColor(context, R.color.black)
            paint.strokeWidth = 0F
            canvas!!.drawLine(
                0.0f, canvas.height.toFloat(), canvas.width.toFloat(),
                canvas.height.toFloat(),
                paint
            )
        }

    }
}