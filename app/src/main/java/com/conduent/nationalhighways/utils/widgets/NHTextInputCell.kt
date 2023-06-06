package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.conduent.apollo.ui.CMTextInput
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.LayoutNhTextInputBinding


/**
 * Created by Shivam Gupta .
 */
open class NHTextInputCell @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CMTextInput(context, attrs!!) {
    lateinit var binding : LayoutNhTextInputBinding
    internal var hint : String? = null
    internal var text : String? = null
    internal var placeholder:String?=null
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
            text = ta.getString(R.styleable.NHTextInputStyle_input_text)
            headingText = ta.getString(R.styleable.NHTextInputStyle_headingText)
            errorText = ta.getString(R.styleable.NHTextInputStyle_errorText)
            placeholder=ta.getString(R.styleable.NHTextInputStyle_placeholderText)
            if (!headingText.isNullOrEmpty()){
                binding.txtPlaceHolderTop.visibility = View.VISIBLE
                binding.txtPlaceHolderTop.text = headingText
            }
            if (!hint.isNullOrEmpty()){
                binding.inputFirstName.hint = hint
            }
            if (!text.isNullOrEmpty()){
                binding.inputFirstName.setText(text.toString())
            }
            if (!errorText.isNullOrEmpty()){
                binding.txtError.visibility = View.VISIBLE
                binding.txtError.text = errorText

            }
        } finally {
            ta.recycle()
        }
    }

/*
    fun getText(): Editable? = binding.inputFirstName.getText()

    fun setText(text: CharSequence){
        binding.inputFirstName.setText(text)
    }
    fun setMaxLength(length:Int){
        binding.inputFirstName.setMaxLength(length)
    }
*/

    fun setHintText(hint: String){
        binding.inputFirstName.hint = hint
    }

    fun getHintText(): CharSequence? = binding.inputFirstName.hint

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
    override fun removeError(){
        binding.txtError.visibility = View.GONE
        binding.txtError.text=""

    }

    fun getErrorText(): String = binding.txtError.text!!.toString()

    fun getInputFirstName():CMTextInput {
        return binding.inputFirstName
    }
}