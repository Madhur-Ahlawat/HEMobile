//package com.conduent.nationalhighways.utils.widgets
//
//import android.content.Context
//import android.util.AttributeSet
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//
//class CustomAutoCompleteTextView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatAutoCompleteTextView(context, attrs) {
//
//    interface OnItemClickListener {
//        fun onItemClick(item: String)
//    }
//
//    private var itemClickListener: OnItemClickListener? = null
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.itemClickListener = listener
//    }
//
//    fun setAutoCompleteAdapter(adapter: ArrayAdapter<String>) {
//        this.setAdapter(adapter)
//    }
//
//    override fun performFiltering(text: CharSequence?, keyCode: Int) {
//        if (text == null || text.isEmpty()) {
//            // If the text is empty, show all items by setting the full list as suggestions
//            val fullAdapter = adapter as? ArrayAdapter<String>
//            super.performFiltering(null, keyCode)
//        } else {
//            // Call the default filtering behavior for non-empty text
//            super.performFiltering(text, keyCode)
//        }
//    }
//
//
//
//    override fun onItemClickListener(position: Int) {
//        super.onItemClickListener(position)
//        val selectedItem = adapter.getItem(position).toString()
//        itemClickListener?.onItemClick(selectedItem)
//    }
//}
