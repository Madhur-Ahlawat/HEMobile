package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

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
                    Log.e(TAG, "onTextChanged: ** " + charSequence.toString())
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
            Log.e(TAG, "onclick: " )
            if (dataSet.size > 0) {
                showDropDown()
            }
        }
        this.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus == false && dataSet.size > 0) {
                if (dataSet.any { it == text.toString() }) {
                } else {
                    setText("")
                    selectedItemDescription = ""
                }
            } else {
//                if (!isPopupShowing && firstTym == false) {
//                    if (dataSet.size > 0) {
//                        showDropDown()
//                    }
//                }
//                if(dataSet.size>0){
//                    firstTym = false
//                }

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

