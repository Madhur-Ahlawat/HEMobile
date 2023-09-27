//package com.conduent.nationalhighways.utils.widgets
//
//import android.content.Context
//import android.graphics.Rect
//import android.util.AttributeSet
//import android.util.Log
//import android.view.MotionEvent
//import android.view.View
//import android.widget.AdapterView
//import androidx.appcompat.widget.AppCompatAutoCompleteTextView
//
//class NHAutoCompleteTextview @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle
//) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr), View.OnTouchListener {
//
//    private lateinit var adapter: CustomAutoCompleteAdapter
////     var dropDownItemSelectListener: AutoCompleteSelectedTextListener? = null
//    open var dataSet: ArrayList<String> = ArrayList()
//
//    init {
//        // Initialize your custom AutoCompleteTextView here
//        // You can set attributes or behavior programmatically
//        threshold = 1
//    }
//
//    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
//        super.onFocusChanged(focused, direction, previouslyFocusedRect)
//        Log.e(TAG, "onFocusChanged: focused "+focused )
//        if(focused){
//            Log.e(TAG, "onTouch: dataSet " + dataSet.size)
//            if (dataSet.isNotEmpty()) {
//                setData(dataSet)
//                showDropDown()
//            }
//        }
//    }
//
//    // Custom method to set data for the AutoCompleteTextView
//    fun setData(data: List<String>) {
//
//        adapter = CustomAutoCompleteAdapter(context, data)
//        setAdapter(adapter)
//
//        adapter.clear()
//        adapter.addAll(data)
//        adapter.notifyDataSetChanged()
//
//    }
//
////    fun setDropDownItemSelectListener(listener: AutoCompleteSelectedTextListener) {
////        dropDownItemSelectListener = listener
////    }
//
//
//    // Override onItemClickListener to handle item selection
//    override fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?) {
//        super.setOnItemClickListener { _, _, position, _ ->
////            listener?.onItemClick(this, selectedView, position, getItemIdAtPosition(position))
//            Log.e(TAG, "setOnItemClickListener: ")
//            // Notify the item selected listener with the selected item
//            val selectedItem = adapter.getItem(position).toString()
////            dropDownItemSelectListener?.onAutoCompleteItemClick(selectedItem)
//        }
//    }
//
//    fun setSelectedValue(selectedItem: String) {
//        setText(selectedItem)
//    }
//
//    fun getSelectedDescription(): String {
//        Log.e(TAG, "getSelectedDescription: text --> " + text.toString())
//        return text.toString()
//    }
//
//}
//
//interface AutoCompleteSelectedTextListener {
//    fun onAutoCompleteItemClick(item: String)
//}