package com.conduent.nationalhighways.utils

import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {


    private val dateFormat = SimpleDateFormat("MM/dd/yyyy")
    val dd_mmm_yyyy_hh_mm_a = "dd MMM yyyy hh:mm a"
    val dd_mmm_yyyy_hh_mm_a_ = "dd MMM yyyy hh:mma"
    val dd_mmm_yyyy = "dd MMM yyyy"
    val dd_mm_yyyy = "MM/dd/yyyy"
    val dd_mmm_yyyy_ = "dd-MMMM-yyyy"
    val mmmm_dd_yyyy = "MMMM dd, yyyy"
    val mm_dd_yyyy_hh_mm_ss_a = "MM/dd/yyyy hh:mm:ss a"
    val yyyy_mm_dd_hh_mm_ss_s = "yyyy-MM-dd hh:mm:ss.s"

    fun currentDate(): String? {
        return dateFormat.format(Calendar.getInstance().time)
    }

    fun currentDateAs(dateFormat: String): String? {
        return SimpleDateFormat(dateFormat, Locale.US).format(Calendar.getInstance().time)
    }

    fun getLast90DaysDate(dateFormat: String): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -31) // Subtract 90 days

        val dateFormat = SimpleDateFormat(dateFormat, Locale.US) // Define the date format
        return dateFormat.format(calendar.time) // Format the date and return as a string
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
            time
        }
    }

    //01/10/2022
    fun convertDateFormat(date: String?, type: Int?): String {
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
            return date ?: ""
        }
    }

    fun compareDates(startDate: String?, endDate: String?): Boolean {
        val dfDate = SimpleDateFormat("dd MMM yyyy hh:mm a")
        var b = false
        try {
            b = if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                true // If start date is before end date.
            } else {
                false // If start date is after the end date.
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return b
    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun sortTransactionsDateWiseDescendingOrder(transactions:MutableList<TransactionData?>){
//        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
//
//        val result = transactions.sortedByDescending {
//            LocalDate.parse(it, dateTimeFormatter)
//        }
//    }

    //01/10/2022
    fun convertMonthNameAndDateFormat(date: String?): String {
        val dateFormatter: DateFormat?
        return try {
            dateFormatter = SimpleDateFormat("MM/dd/yyyy")
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("d MMM")
            postFormatter.format(dateObj)
        } catch (e: Exception) {
            date ?: ""
        }
    }


    fun getDateForCasesAndEnquiry(date: String?): String {
        val list = mutableListOf<DateFormat>()
        val dateFormatter1: DateFormat = SimpleDateFormat("MMM dd,yyyy, hh:mm")
        val dateFormatter2: DateFormat = SimpleDateFormat("dd MMM yyyy hh:mm aa")
        list.add(dateFormatter1)
        list.add(dateFormatter2)

        list.forEach { myDate ->
            try {
                val dateObj = myDate.parse(date)
                val postFormatter = SimpleDateFormat("dd MMM yyyy")
                return postFormatter.format(dateObj)
            } catch (e: Exception) {

            }
        }
        return date ?: ""
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

    fun convertDateToFullDate(date: String): String {
        val dateFormatter: DateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a")
        return try {
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("dd MMM yyyy hh:mm a")
            postFormatter.format(dateObj).replace("AM", "am").replace("PM", "pm")
        } catch (e: Exception) {
            date.replace("AM", "am").replace("PM", "pm")
        }
    }

    fun convertStringDatetoAnotherFormat(
        date: String,
        dateFormat1: String,
        dateFormat2: String
    ): String {
        Log.e(
            "TAG",
            "convertStringDatetoAnotherFormat() called with: date = $date, dateFormat1 = $dateFormat1, dateFormat2 = $dateFormat2"
        )
        val dateFormatter: DateFormat = SimpleDateFormat(dateFormat1)
        return try {
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat(dateFormat2)
            postFormatter.format(dateObj).replace("AM", "am").replace("PM", "pm")
        } catch (e: Exception) {
            date
        }
    }

    fun convertDateFormatToDateFormat(date: String): String {
        val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd h:mm:ss")
        return try {
            val dateObj = dateFormatter.parse(date)
            val postFormatter = SimpleDateFormat("dd MMM yyyy")
            postFormatter.format(dateObj)
        } catch (e: Exception) {
            date
        }
    }

    fun convertDateToString(date: Date, dateFormat: String): String {

        val dateFormat = SimpleDateFormat(dateFormat) // Define your desired date format
        return dateFormat.format(date) // Convert Date to String

    }

}