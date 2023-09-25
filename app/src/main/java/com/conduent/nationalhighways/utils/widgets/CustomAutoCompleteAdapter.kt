package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.conduent.nationalhighways.R


class CustomAutoCompleteAdapter(context: Context, private val data: List<String>) :
    ArrayAdapter<String>(context, R.layout.custom_autocomplete_item, data), Filterable {

    private val originalItems: List<String> = ArrayList(data)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.custom_autocomplete_item, parent, false)

        // Customize the item view here if needed.
        val textView: TextView = rowView.findViewById(R.id.custom_textview)
        textView.text = data[position]

        return rowView
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = ArrayList<String>()

                if (constraint == null || constraint.isEmpty()) {
                    suggestions.addAll(originalItems)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()

                    for (item in originalItems) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            suggestions.add(item)
                        }
                    }
                }
                Log.e(TAG, "performFiltering: suggestions "+suggestions )
                results.values = suggestions
                results.count = suggestions.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results != null && results.count > 0) {
                    @Suppress("UNCHECKED_CAST")

                    val filteredList = results.values as List<String>
                    addAll(filteredList)
                }
                notifyDataSetChanged()
            }
        }
    }

}