package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.conduent.nationalhighways.R


/**
 * Created by Mohammed Sameer Ahmad .
 */
open class NHTextInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    internal var hint : String? = null
    internal var title : String? = null
    private var headingText : String? = null
    private var errorText : String? = null
    lateinit var txtHeading : TextView
    lateinit var txtError : TextView
    lateinit var editText : EditText
    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.layout_nh_text_input, this)
        txtHeading = findViewById(R.id.txtPlaceHolderTop)
        txtError = findViewById(R.id.txtError)
        editText = findViewById(R.id.editText)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.NHTextInputStyle)
        try {
            hint = ta.getString(R.styleable.NHTextInputStyle_hintText)
            title = ta.getString(R.styleable.NHTextInputStyle_title)
            headingText = ta.getString(R.styleable.NHTextInputStyle_headingText)
            errorText = ta.getString(R.styleable.NHTextInputStyle_errorText)
//            val drawableLeftId = ta.getResourceId(R.styleable.NHTextInput_drawableLeft, 0)
//            if (drawableLeftId != 0) {
//                val drawable = AppCompatResources.getDrawable(context, drawableLeftId)
//                editText.setCompoundDrawablesWithIntrinsicBounds(left = drawable, top = 0,right = 0,bottom = 0)
//            }
            if (!headingText.isNullOrEmpty()){
                txtHeading.visibility = View.VISIBLE
                txtHeading.text = headingText
            }
            if (!hint.isNullOrEmpty()){
                editText.hint = hint
            }
            if (!title.isNullOrEmpty()){
                editText.setText(title)
            }
            if (!errorText.isNullOrEmpty()){
                txtError.visibility = View.VISIBLE
                txtError.text = errorText

            }
        } finally {
            ta.recycle()
        }
    }

    fun getText(): Editable? = editText.text

    fun setText(text: CharSequence){
        editText.setText(text)
    }

    fun setHintText(hint: String){
        editText.hint = hint
    }

    fun getHintText(): CharSequence? = editText.hint

    fun setHeadingText(text: CharSequence?){
        if (text?.isNotEmpty() == true){
            txtHeading.visibility = View.VISIBLE
            txtHeading.text = text.toString()
        }
    }

    fun getHeadingText(): String = txtHeading.text!!.toString()

    fun setErrorText(errorText: String){
        if (errorText.isNotEmpty()){
            txtError.visibility = View.VISIBLE
            txtError.text = errorText
        }
    }

    fun getErrorText(): String = txtError.text!!.toString()


}