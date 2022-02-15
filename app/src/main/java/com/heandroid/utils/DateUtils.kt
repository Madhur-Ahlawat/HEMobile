package com.heandroid.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.heandroid.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit
import android.provider.Settings.System.DATE_FORMAT
import java.text.DateFormat
import java.text.ParseException


object DateUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun currentDate() : String?{
        return dateFormat.format(Calendar.getInstance()?.time)
    }

    fun lastPriorDate(day: Int) : String? {
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.DATE,day+(calendar.get(Calendar.DAY_OF_MONTH)))
        return dateFormat.format(calendar.time)
    }

    fun calculateDays(from: String?,to: String?){
        val date1: Date = dateFormat.parse(from)
        val date2: Date = dateFormat.parse(to)
        val diff = date2.time - date1.time
        println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))
    }

    fun getRangeBetweenDate(from: String?, to: String?): List<Date?> {
        val dates = ArrayList<Date>()
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = dateFormat.parse(from)
            date2 = dateFormat.parse(to)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2)) {
            dates.add(cal1.time)
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }
}