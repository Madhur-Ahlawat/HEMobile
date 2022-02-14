package com.heandroid.utils

import android.content.Context
import android.os.Build
import com.heandroid.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun currentDate() : String?{
        return dateFormat.format(Calendar.getInstance()?.time)
    }

    fun lastPriorDate(day: Int) : String? {
        val tenDaysAgo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().minusDays(day.toLong())
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val calendar=Calendar.getInstance()
//        calendar.time=Calendar.getInstance().time
        calendar.set(Calendar.DATE,-5)
        return dateFormat.format(calendar.time)
    }

    fun calculateDays(from: String?,to: String?){
        val date1: Date = dateFormat.parse(from)
        val date2: Date = dateFormat.parse(to)
        val diff = date2.time - date1.time
        println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))
    }
}