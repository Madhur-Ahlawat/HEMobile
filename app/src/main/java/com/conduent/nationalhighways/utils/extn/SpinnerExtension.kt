package com.conduent.nationalhighways.utils.extn

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R


fun Spinner.setSpinnerAdapter(list: List<String>) {
    val adapter = object : ArrayAdapter<String?>(this.context, android.R.layout.simple_spinner_dropdown_item, list.toTypedArray<String?>()) {
        override fun isEnabled(position: Int): Boolean = position != 0

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View = super.getDropDownView(position, convertView, parent)
            if(view is TextView){
                if (position == 0) view.setTextColor(ContextCompat.getColor(this.context, R.color.E5E5E5))
                else view.setTextColor(ContextCompat.getColor(this.context, R.color.black))
            }
            return view
        }
    }

    this.adapter = adapter
}
