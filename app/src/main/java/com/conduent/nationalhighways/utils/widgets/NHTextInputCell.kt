package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.LayoutNhTextInputBinding


/**
 * Created by Mohammed Sameer Ahmad .
 */
open class NHTextInputCell @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    lateinit var binding : LayoutNhTextInputBinding
    internal var hint : String? = null
    internal var title : String? = null
    internal var headingText : String? = null
    internal var errorText : String? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        binding = LayoutNhTextInputBinding.inflate(LayoutInflater.from(context), this)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NHTextInputStyle)
        try {
            hint = ta.getString(R.styleable.NHTextInputStyle_hintText)
            title = ta.getString(R.styleable.NHTextInputStyle_title)
            headingText = ta.getString(R.styleable.NHTextInputStyle_headingText)
            errorText = ta.getString(R.styleable.NHTextInputStyle_errorText)
            if (!headingText.isNullOrEmpty()){
                binding.txtPlaceHolderTop.visibility = View.VISIBLE
                binding.txtPlaceHolderTop.text = headingText
            }
            if (!hint.isNullOrEmpty()){
                binding.editText.hint = hint
            }
            if (!title.isNullOrEmpty()){
                binding.editText.setText(title)
            }
            if (!errorText.isNullOrEmpty()){
                binding.txtError.visibility = View.VISIBLE
                binding.txtError.text = errorText

            }
        } finally {
            ta.recycle()
        }
    }

    fun getText(): Editable? = binding.editText.text

    fun setText(text: CharSequence){
        binding.editText.setText(text)
    }

    fun setHintText(hint: String){
        binding.editText.hint = hint
    }

    fun getHintText(): CharSequence? = binding.editText.hint

    fun setHeadingText(text: CharSequence?){
        if (text?.isNotEmpty() == true){
            binding.txtPlaceHolderTop.visibility = View.VISIBLE
            binding.txtPlaceHolderTop.text = text.toString()
        }
    }

    fun getHeadingText(): String = binding.txtPlaceHolderTop.text!!.toString()

    fun setErrorText(errorText: String){
        if (errorText.isNotEmpty()){
            binding.txtError.visibility = View.VISIBLE
            binding.txtError.text = errorText
        }
    }

    fun getErrorText(): String = binding.txtError.text!!.toString()

    fun getEditText(): NHTextInput {
        return binding.editText
    }
}