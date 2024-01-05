package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.conduent.nationalhighways.utils.common.Constants

class NHAutoCompleteTextview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle
) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {

    private lateinit var adapter: CustomAutoCompleteAdapter
    private var dropDownItemSelectListener: AutoCompleteSelectedTextListener? = null
    open var dataSet: ArrayList<String> = ArrayList()
    open var selectedItemDescription: String = ""
    private var firstTym = true

    init {
        // Initialize your custom AutoCompleteTextView here
        // You can set attributes or behavior programmatically
        threshold = 1
        isSingleLine = true
        imeOptions = EditorInfo.IME_ACTION_DONE
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                charSequence?.let {
                    dropDownItemSelectListener?.onAutoCompleteItemClick(
                        charSequence.toString(),
                        false
                    )

                }
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        this.setOnClickListener {
            if (dataSet.size > 0) {
                showDropDown()
            }
        }
        this.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (dataSet.size > 0) {
                    if (dataSet.any { it == text.toString() }) {
                    } else {
                        setText("")
                        selectedItemDescription = ""
                    }
                } else {
                    if(text.toString() != Constants.UNITED_KINGDOM){
                        setText("")
                        selectedItemDescription = ""
                    }

                }

            } else {
            }
        }
    }

    override fun enoughToFilter(): Boolean {
        return true
    }


    // Custom method to set data for the AutoCompleteTextView
    fun setData(data: List<String>) {
        adapter = CustomAutoCompleteAdapter(context, data)
        setAdapter(adapter)
        adapter.notifyDataSetChanged()
        onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        }

    }

    fun setDropDownItemSelectListener(listener: AutoCompleteSelectedTextListener) {
        dropDownItemSelectListener = listener
        setData(dataSet)
//        showDropDown()
    }


    // Override onItemClickListener to handle item selection
    override fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?) {
        super.setOnItemClickListener { _, _, position, _ ->
            // Notify the item selected listener with the selected item
            selectedItemDescription = adapter.getItem(position).toString()
            dropDownItemSelectListener?.onAutoCompleteItemClick(selectedItemDescription, true)
        }
    }

    fun setSelectedValue(selectedItem: String) {
        setText(selectedItem)
        selectedItemDescription = selectedItem
    }

    fun getSelectedDescription(): String {
        return text.toString()
    }

    interface AutoCompleteSelectedTextListener {
        fun onAutoCompleteItemClick(item: String, selected: Boolean)
    }

}

