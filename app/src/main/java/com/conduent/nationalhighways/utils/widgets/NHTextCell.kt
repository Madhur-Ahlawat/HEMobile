package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.databinding.NhtextcellBinding

open class NHTextCell @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    internal var title: TextView? = null
    internal var text: TextView? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.nhtextcell, this)

        text = findViewById(R.id.text)
        title = findViewById(R.id.title)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.NHTextCellStyle)
        try {
            text?.text = ta.getString(R.styleable.NHTextCellStyle_text)
            title?.text = ta.getString(R.styleable.NHTextCellStyle_title)


        } finally {
            ta.recycle()
        }
    }
}