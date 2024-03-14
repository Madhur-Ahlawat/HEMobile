package com.conduent.nationalhighways.utils

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan


class CustomTypefaceSpan(private val typeface: Typeface, private val color: Int) :
    MetricAffectingSpan() {

    override fun updateMeasureState(p: TextPaint) {
        p.typeface = typeface
        p.color = color

    }

    override fun updateDrawState(tp: TextPaint) {
        tp.typeface = typeface
        tp.color = color
    }
}