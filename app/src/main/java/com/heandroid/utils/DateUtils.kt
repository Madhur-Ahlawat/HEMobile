package com.heandroid.utils

import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy")

    fun currentDate(): String? {
        return dateFormat.format(Calendar.getInstance()?.time)
    }

    fun lastPriorDate(day: Int): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DATE, day + (calendar.get(Calendar.DAY_OF_MONTH)))
        return dateFormat.format(calendar.time)
    }

    fun calculateDays(from: String?, to: String?) {
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

    // 02:20:00 AM
    fun convertTimeFormat(time: String, type: Int): String {
        val timeFormatter: DateFormat?
        return try {
            timeFormatter = if (type == 0) {
                SimpleDateFormat("hh:mm:ss a")
            } else {
                SimpleDateFormat("h:mm:ss")

            }
            val dateObj = timeFormatter.parse(time)
            val postFormatter = SimpleDateFormat("HH:mm")
            postFormatter.format(dateObj)
        } catch (e: Exception) {
            "__:__"
        }
    }

    //01/10/2022
    fun convertDateFormat(date: String, type: Int): String {
        val dateFormatter: DateFormat?
        try {
            dateFormatter = when (type) {
                0 -> {
                    SimpleDateFormat("MM/dd/yyyy")
                }
                1 -> {
                    SimpleDateFormat("MM/dd/yyyy hh:mm aa") //"03/03/2022 01:07 AM"
                }
                else -> {
                    SimpleDateFormat("MM/dd/yyyy")
                }
            }
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("dd MMM yyyy")
            return postFormatter.format(dateObj)
        } catch (e: Exception) {
            return "__ ___ ____"
        }
    }

    fun convertDateToMonth(date: String): String {
        val dateFormatter: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return try {
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("MM/dd/yyyy")
            postFormatter.format(dateObj)
        } catch (e: Exception) {
            date
        }
    }

    fun convertDateToDate(date: String): String {
        val dateFormatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        return try {
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("dd/MM/yyyy")
            postFormatter.format(dateObj)
        } catch (e: Exception) {
            date
        }
    }

}