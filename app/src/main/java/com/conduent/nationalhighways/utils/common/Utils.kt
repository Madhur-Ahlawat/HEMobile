package com.conduent.nationalhighways.utils.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.extn.changeBackgroundColor
import com.conduent.nationalhighways.utils.extn.changeTextColor
import java.lang.reflect.Field
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {

    var PASSWORD_RULE1 = ".{8,64}"
    var LOWER_CASE = "qwertyuiopasdfghjklzxcvbnm"
    var UPPER_CASE = "QWERTYUIOPASDFGHJKLZXCVBNM"
    var UK_MOBILE_REGEX: Regex = Regex("[0]{0,1}7[0-9]{9}")
    var PHONENUMBER: Regex = Regex("[0]{0,3}[1-9]{1}[0-9]{7,14}")
    var NUMBER: Regex = Regex(".*[0-9]+.*")
    var UPPERCASE: Regex = Regex(".*[A-Z]+.*")
    var SPECIAL_CHARACTERS_FOR_MODEL = "!\$%^*<>?;{}[]\\\\|~,`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var TWO_OR_MORE_DOTS: Regex = Regex("[\\.]+[\\.]+")
    var TWO_OR_MORE_HYPEN: Regex = Regex("[\\-]+[\\-]+")
    var LOWECASE: Regex = Regex(".*[a-z]+.*")
    var SPECIAL_CHARACTERS_FOR_MAKE = "!@#\$%^&*<>?;:{}[]\\\\|~`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS = "!@#\$%^&*()+<>?/;:{}[]\\\\|~',.-`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_FOR_COLOR = "!@#\$%^&*()+<>?;:{}[]\\\\|~'\",._-`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_FOR_COMPANY_NAME = "#\$%^*()+<>/{}[]\\\\|~`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_FOR_NAME = "!@#\$%^&*()+<>?/;:{}[]\\\\|~'_,.-`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_ADDRESS = "!@\$%^&*()+<>?/;#\":{}[]\\\\|_~`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_TOWN_OR_CITY = "#!@\$%^&*()+<>?,\"./;:{}[]\\\\|_~`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var SPECIAL_CHARACTERS_POSTCODE = "!@#\$%^&*()+<>?/;:{}[]\\\\|~',\"._`•√π÷×§∆£¢€¥^°=\\©®™✓"
    var DIGITS = "0123456789"
    var SPECIAL_CHARACTERS_ALLOWED_IN_PASSWORD = "~!@#\$%^&*_-+=`|\\(){}[]:;\"'<>,.?/`•√π÷×§∆£¢€¥^°\\©®™✓"
    var ALPHABETS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
    var passwordRegEX: Regex =
        Regex("^(?=.*?[0-9])(?=.*[a-z])(?=.*[A-Z])[\\w~!@#$%^&*_\\-+=`|\\(){}\\[\\]:;\"'<>,.?\\/]{8,20}$")

    fun hasSpecialCharacters(str: String,specialCharacterString:String): Boolean {
        var hasSpecialChar=false
        str.forEach { char ->

            if (specialCharacterString.contains(char.toString())) {
                hasSpecialChar=true
                return@forEach
            }
        }
        return hasSpecialChar
    }
    fun hasLowerCase(str: String): Boolean {
        var hasSpecialChar=false
        str.forEach { char ->
            if (LOWER_CASE.contains(char)) {
                hasSpecialChar=true
                return@forEach
            }
        }
        return hasSpecialChar
    }
    fun hasUpperCase(str: String): Boolean {
        var hasSpecialChar=false
        str.forEach { char ->
            if (UPPER_CASE.contains(char)) {
                hasSpecialChar=true
                return@forEach
            }
        }
        return hasSpecialChar
    }
    fun hasDigits(str: String): Boolean {
        var hasSpecialChar=false
        str.forEach { char ->
            if (DIGITS.contains(char)) {
                hasSpecialChar=true
            }
            else{
                hasSpecialChar=false
            }
        }
        return hasSpecialChar
    }
    fun hasAlphabets(str: String): Boolean {
        var hasSpecialChar=false
        str.forEach { char ->
            if (ALPHABETS.contains(char)) {
                hasSpecialChar=true
            }
            else{
                hasSpecialChar=false
            }
        }
        return hasSpecialChar
    }

    var specialCharacter: Regex = Regex("[0-9$&+,:;=\\\\?@#|/'<>.^*()%!]")
    var excludeNumber: Regex = Regex("[$&+,:;=\\\\?@#|/'<>.^*()%!]")

    var colourSpecialCharacter: Regex = Regex("[$&\\[\\]+,:;=\\\\?@#|'<>.^*()%!{_}-]")
    var addressSpecialCharacter: Regex = Regex("[\${_&\\[\\]+:;=\\\\?@|/<>^*()%!}]")


    fun hasInternetConnection(application: BaseApplication): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private val EMAIL = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )
    private const val MIN_PASSWORD_LENGTH = 6


    fun isEmailValid(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            EMAIL.matcher(email).matches()
        }
    }

    fun isPasswordValid(password: String?): Boolean {
        return if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            false
        } else {
            password.trim { it <= ' ' }.length >= MIN_PASSWORD_LENGTH
        }
    }


    fun currentDateAndTime(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.getDefault())
        return sdf.format(Date())

    }

    fun getDirection(entry: String?): String {
        return when (entry) {
            "N" -> "NORTHBOUND"
            "S" -> "SOUTHBOUND"
            else -> ""
        }
    }

    fun loadStatus(status: String, tvTitle: AppCompatTextView) {
        when (status) {

            "Y" -> {
                tvTitle.text = tvTitle.context.getString(R.string.paid)
                tvTitle.changeTextColor(R.color.color_10403C)
                tvTitle.changeBackgroundColor(R.color.color_CCE2D8)


            }

            "N" -> {
                tvTitle.text = tvTitle.context.getString(R.string.unpaid)
                tvTitle.changeTextColor(R.color.color_10403C)
                tvTitle.changeBackgroundColor(R.color.FCD6C3)

            }

            else -> {
                tvTitle.text = status
                tvTitle.changeTextColor(R.color.color_594D00)
                tvTitle.changeBackgroundColor(R.color.FFF7BF)
            }
        }
    }

    fun sessionExpired(context: AppCompatActivity) {
        context.startActivity(
            Intent(context, LandingActivity::class.java)
                .putExtra(Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.TYPE, Constants.LOGIN)
        )
    }
    fun removeGivenStringCharactersFromString(characterString:String,input:String): String {
        var input=input
        characterString.forEach {
            input.replace(it.toString(),"")
        }
        return input
    }
    fun makeCommaSeperatedStringForPassword(input:String): String {
        var output=""
        input.forEachIndexed { index, c ->
            if(input.length==1){
                output=output+c.toString()
            }
            else if(index<input.length){
                output=output+c.toString()+", "
            }
            else{
                output=output+c.toString()

            }
        }
        return output
    }
//    fun getOccuringChar(str: String): Int {
////creating an array of size 256 (ASCII_SIZE)
//        val count = IntArray(256)
//        //finds the length of the string
//        val len = str.length
//        //initialize count array index
//        for (i in 0 until len) count[str[i].code]++
//        //create an array of given String size
//        val ch = CharArray(str.length)
//        for (i in 0 until len) {
//            ch[i] = str[i]
//            var find = 0
//            for (j in 0..i) {
////if any matches found
//                if (str[i] == ch[j]) find++
//            }
//            if (find == 1) //prints occurrence of the character
//                return count[str[i].code]
//            else
//                return 0
//        }
//    }

    fun getStatusForCases(status: String, tvTitle: AppCompatTextView) {
        when (status) {
            "Closed" -> {
                tvTitle.text = tvTitle.context.getString(R.string.resolved)
                tvTitle.changeTextColor(R.color.color_3D2375)
                tvTitle.changeBackgroundColor(R.color.color_DBD5E9)
            }

            "Open" -> {
                tvTitle.text = tvTitle.context.getString(R.string.submitted)
                tvTitle.changeTextColor(R.color.color_10403C)
                tvTitle.changeBackgroundColor(R.color.color_CCE2D8)
            }

            else -> {
                tvTitle.text = tvTitle.context.getString(R.string.in_progress_open)
                tvTitle.changeTextColor(R.color.color_594D00)
                tvTitle.changeBackgroundColor(R.color.FFF7BF)
            }
        }
    }


    fun isValidPassword(password: String?): Boolean {
//        val PASSWORD_PATTERN = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+*!=]).*$"
        val PASSWORD_PATTERN = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z]).*$"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun getManuallyAddedVehicleClass(vehicleClass: String): String {
        return when (vehicleClass) {
            "Motorcycle, moped or quad bike" -> {
                "A"
            }

            "Car, van or minibus < 8 seats" -> {
                "B"
            }

            "Bus, coach or other goods vehicle with 2 axles" -> {
                "C"
            }

            "Vehicle with more than 2 axles" -> {
                "D"
            }

            else -> {
                ""
            }
        }
    }

    fun getVehicleType(vehicleClass: String): String {
        return when (vehicleClass) {
            "A" -> {
                "Motorcycle, moped or quad bike"
            }

            "B" -> {
                "Car, van or minibus < 8 seats"
            }

            "C" -> {
                "Bus, coach or other goods vehicle with 2 axles"
            }

            "D" -> {
                "Vehicle with more than 2 axles"
            }

            else -> {
                ""
            }
        }
    }

    fun getVehicleTypeNumber(vehicleClass: String): String {
        return when (vehicleClass) {
            "A" -> {
                "1"
            }

            "B" -> {
                "2"
            }

            "C" -> {
                "3"
            }

            "D" -> {
                "4"
            }

            else -> {
                ""
            }
        }
    }

    fun validateString(target: String, pattern: String): Boolean {
        val mPattern =
            Pattern.compile(pattern)
        return mPattern.matcher(target).matches()
    }

    fun mobileNumber(mobNo: String?): String {
        val regexPattern =
            "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
        val match: Matcher
        val pattern = Pattern.compile(regexPattern)
        match = pattern.matcher(mobNo)
        if (match.find()) {
            return "Password matched"
        }
        return "Password not matched"

    }

    fun getVersionName(): String {
        val fields: Array<Field> = VERSION_CODES::class.java.fields
        var versionNumber = "1"
        for (field in fields) {
            var fieldValue = -1
            try {
                fieldValue = field.getInt(Any())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                versionNumber = fieldValue.toString()
            }
        }
        return versionNumber
    }

    fun getFileUploadMIMETypes(): Array<String> {
        val list = mutableListOf<String>()
        list.add(DOC)
        list.add(DOCX)
        list.add(XLS)
        list.add(XLSX)
        list.add(PDF)
        list.add(IMAGE)
        list.add(IMAGE_JPEG)
        list.add(IMAGE_TIFF)
        list.add(IMAGE_BMP)
        list.add(CSV)
        return list.toTypedArray()
    }

    fun gotoMobileSetting(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun maskEmail(email: String): String {

        val arrEmail = email.split("@")
        val mailbox = arrEmail[0].subSequence(0, 1).toString() + "*".repeat(arrEmail[0].length - 1)

        val domain = arrEmail[1].split(".")[0]

        val maskedDomain = domain.subSequence(0, 1).toString() + "*".repeat(domain.length - 1)


        return mailbox + "@" + maskedDomain + arrEmail[1].replace(domain, "")
    }

    fun maskPhoneNumber(phoneNumber: String): String {
        val star = phoneNumber.length - 6
        return if (star == 4) {
            phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "*****")

        } else {
            phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "******")

        }


    }

    fun maskCardNumber(cardNumber: String): String {
        return cardNumber.replace(cardNumber.substring(0, cardNumber.length - 4), "************")


    }



    fun getYesterdayDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("dd MMMM yyyy")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }


    private const val DOC = "application/msword"
    private const val DOCX =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    private const val XLS = "application/vnd.ms-excel"
    private const val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    private const val PDF = "application/pdf"
    private const val IMAGE = "image/*"
    private const val IMAGE_JPEG = "image/jpeg"
    private const val IMAGE_TIFF = "image/tiff"
    private const val IMAGE_BMP = "image/bmp"
    private const val CSV = "text/csv"
    private const val AUDIO = "audio/*"
    private const val TEXT = "text/*"

    fun convertToPoundFormat(currentBalance: String?): CharSequence? {
        var balance = currentBalance
        if (balance.isNullOrEmpty()) {
            return "£ 0.00"
        }
        try {
            balance = currentBalance?.replace("£", "")?.replace(",", "")
            val number = NumberFormat.getCurrencyInstance(Locale.UK)

            return if (balance?.contains("(") == true && balance.contains(")")) {
                val doublePayment = balance?.replace("(", "")?.replace(")", "")?.toDouble()
                number.format(doublePayment)
            } else {
                val doublePayment = balance?.toDouble()
                number.format(doublePayment)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "£$balance"
    }



}