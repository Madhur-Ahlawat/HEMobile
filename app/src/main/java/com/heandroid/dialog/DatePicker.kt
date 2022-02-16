package com.heandroid.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*


class DatePicker(var tv: TextView?) : DialogFragment(), OnDateSetListener {
    private val calender: Calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = DatePickerDialog(requireActivity(),AlertDialog.THEME_HOLO_LIGHT, this,
                                calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                                calender.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.maxDate = Date().time
        return dialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
       val format= SimpleDateFormat("dd/MM/yyyy")
       val date=format.parse("${dayOfMonth}/${month+1}/${year}")
       tv?.text=format.format(date)
    }
}