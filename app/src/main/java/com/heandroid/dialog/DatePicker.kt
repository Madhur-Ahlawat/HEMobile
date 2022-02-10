package com.heandroid.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.DatePicker
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


class DatePicker(var tv: TextView?) : DialogFragment(), OnDateSetListener {
    private val calender: Calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireActivity(),AlertDialog.THEME_HOLO_LIGHT, this,
                                calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                                calender.get(Calendar.DAY_OF_MONTH))
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
       val format= SimpleDateFormat("dd/MM/yyyy")
       val date=format.parse("${dayOfMonth}/${month}/${year}")
       tv?.text=format.format(date)
    }
}