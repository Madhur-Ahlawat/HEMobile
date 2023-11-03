package com.conduent.nationalhighways.utils.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationManagerCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.databinding.DialogRetryBinding
import com.conduent.nationalhighways.databinding.DialogSessionexpiryBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.extn.changeBackgroundColor
import com.conduent.nationalhighways.utils.extn.changeTextColor
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import com.conduent.nationalhighways.utils.widgets.NHTextInputCell
import com.google.firebase.crashlytics.internal.common.CommonUtils
import okhttp3.Interceptor
import java.io.File
import java.lang.reflect.Field
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {
    val amountFormatter = DecimalFormat("#,###.00")
    private var ALLOWED_CHARS_BUILDING_STREE_NO = "\',.-"
    private var ALLOWED_CHARS_ADDRESS_LINE_2 = "\',.-"
    private var ALLOWED_CHARS_TOWN_OR_CITY = "-.,\'"
    private var ALLOWED_CHARS_POSTCODE = "-"
    private var ALLOWED_CHARS_COMPANY_NAME = "-\'.,:;?!&@"
    private var ALLOWED_CHARS_VEHICLE_MAKE = "-._/()+\'"
    private var ALLOWED_CHARS_VEHICLE_MODEL = "&-.@:_/()#+\'"
    private var ALLOWED_CHARS_VEHICLE_COLOR = "/"
    private var ALLOWED_CHARS_VEHICLE_REGISTRATION_PLATE = "-"
    var ALLOWED_CHARS_PASSWORD =
        "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890~!@#\$%^&*_-+=`|\\(){}[]:;\"\'<>,.?/"
    var ALLOWED_CHARS_EMAIL = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM@-._+"
    var SPECIAL_CHARACTERS = "!@#\$%^&*₹()+<>?/;:{}[]\\\\|~\"_\',.-`•√π÷×§∆£¢€¥^°=\\©®™✓"


    var PASSWORD_RULE1 = ".{8,64}"
    var LOWER_CASE = "qwertyuiopasdfghjklzxcvbnm"
    var UPPER_CASE = "QWERTYUIOPASDFGHJKLZXCVBNM"
    var UK_MOBILE_REGEX: Regex = Regex("[0]{0,1}7[0-9]{9}")
    var ACCOUNT_NAME_FIRSTNAME_LASTNAME: Regex = Regex("[a-zA-Z\\-]+")
    var PHONENUMBER: Regex = Regex("[0]{0,3}[1-9]{1}[0-9]{7,14}")
    var NUMBER: Regex = Regex(".*[0-9]+.*")
    var UPPERCASE: Regex = Regex(".*[A-Z]+.*")
    var TWO_OR_MORE_DOTS: Regex = Regex("[\\.]+[\\.]+")
    var TWO_OR_MORE_AT_THE_RATE: Regex = Regex("[@]+[@]+")
    var TWO_OR_MORE_HYPEN: Regex = Regex("[\\-]+[\\-]+")
    var LOWECASE: Regex = Regex(".*[a-z]+.*")
    var DIGITS = "0123456789"
    var SPECIAL_CHARACTERS_ALLOWED_IN_PASSWORD =
        "~!@#\$%^&*_-+=`|\\(){}[]:;\"'<>,.?/`•√π÷×§∆£¢€¥^°\\©®™✓"
    var ALPHABETS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
    var passwordRegEX: Regex =
        Regex("^(?=.*?[0-9])(?=.*[a-z])(?=.*[A-Z])[\\w~!@#$%^&*_\\-+=`|\\(){}\\[\\]:;\"'<>,.?\\/]{8,20}$")

    val splCharAddress1: String by lazy {
        ALLOWED_CHARS_BUILDING_STREE_NO
//        getSplCharString(ALLOWED_CHARS_BUILDING_STREE_NO)
    }

    val splCharAddress2: String by lazy {
        ALLOWED_CHARS_ADDRESS_LINE_2
//        getSplCharString(ALLOWED_CHARS_ADDRESS_LINE_2)
    }

    val splCharTownCity: String by lazy {
        ALLOWED_CHARS_TOWN_OR_CITY
//        getSplCharString(ALLOWED_CHARS_TOWN_OR_CITY)
    }

    val splCharPostCode: String by lazy {
        ALLOWED_CHARS_POSTCODE
//        getSplCharString(ALLOWED_CHARS_POSTCODE)
    }

    fun capitalizeString(str: String?): String? {
        return if (str == null || str.length == 0) str else str.substring(0, 1)
            .uppercase(Locale.getDefault()) + str.substring(1).lowercase(Locale.getDefault())
    }

    @RequiresApi(VERSION_CODES.O)
    fun sortTransactionsDateWiseDescending(transactions: MutableList<TransactionData>): MutableList<TransactionData> {
        var transactionListSorted: MutableList<TransactionData> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted.isEmpty() == true) {
                transaction.showDateHeader = true
                transactionListSorted.add(transaction)
            } else {
                if (DateUtils.compareDates(
                        transactionListSorted.last().transactionDate + " " + transactionListSorted.last().exitTime,
                        transaction.transactionDate + " " + transaction.exitTime
                    )
                ) {
                    transactionListSorted.add(transactionListSorted.get(transactionListSorted.size - 1))
                    transactionListSorted.add(transactionListSorted.size - 2, transaction)

                } else {
                    transactionListSorted.add(transaction)
                }
            }

        }
        return transactionListSorted
    }

    @RequiresApi(VERSION_CODES.O)
    fun sortAlertsDateWiseDescending(transactions: MutableList<AlertMessage>): MutableList<AlertMessage> {
        var transactionListSorted: MutableList<AlertMessage> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted.isEmpty() == true) {
                transactionListSorted.add(transaction)
            } else {
                if (DateUtils.compareDates(
                        transactionListSorted.last().createTs,
                        transaction.createTs
                    )
                ) {
                    transactionListSorted.add(transactionListSorted.size - 1, transaction)

                } else {
                    transactionListSorted.add(transaction)
                }
            }

        }
        return transactionListSorted
    }

    fun validateAmount(
        nhTextInputCell: NHTextInputCell,
        minimumAmount: Double,
        isTopUp: Boolean
    ): Boolean {
        var isValid = false
        var mText = nhTextInputCell.editText.text.toString().trim()
        Log.e("TOPUP", mText)
        mText =
            mText.replace("$", "").replace("£", "").replace("£.", "").replace(",", "")
                .replace(" ", "")
        Log.e("TOPUP", mText + "\n\n")
        if (mText.isNotEmpty()) {
            if (mText.length == 1 && mText.equals(".")) {
                mText = "0"
            }
            isValid = if (mText.toDouble().toInt() <= 100000) {

                if (mText.toDouble() < minimumAmount) {
                    if (isTopUp) {
                        nhTextInputCell.setErrorText(
                            nhTextInputCell.context.getString(
                                R.string.str_top_up_amount_must_be_more,
                                minimumAmount.toString()
                            )
                        )
                    } else {
                        nhTextInputCell.setErrorText(
                            nhTextInputCell.context.getString(
                                R.string.str_low_balance_must_be_more,
                                minimumAmount.toString()
                            )
                        )
                    }
                    false

                } else {
                    nhTextInputCell.removeError()
                    true
                }
            } else {
                if (isTopUp) {
                    nhTextInputCell.setErrorText(nhTextInputCell.context.getString(R.string.top_up_amount_must_be_80_000_or_less))
                } else {
                    nhTextInputCell.setErrorText(nhTextInputCell.context.getString(R.string.low_balance_amount_must_be_80_000_or_less))
                }
                false
            }
        } else {
            nhTextInputCell.removeError()
        }
        return isValid
    }

    val splCharEmailCode: String by lazy {
        ALLOWED_CHARS_EMAIL
//        getSplCharString(ALLOWED_CHARS_EMAIL)
    }
    val splCharCompanyName: String by lazy {
        ALLOWED_CHARS_COMPANY_NAME
//        getSplCharString(ALLOWED_CHARS_COMPANY_NAME)
    }
    val splCharVehicleMake: String by lazy {
        ALLOWED_CHARS_VEHICLE_MAKE
//        getSplCharString(ALLOWED_CHARS_VEHICLE_MAKE)
    }
    val splCharVehicleModel: String by lazy {
        ALLOWED_CHARS_VEHICLE_MODEL
//        getSplCharString(ALLOWED_CHARS_VEHICLE_MODEL)
    }
    val splCharVehicleColor: String by lazy {
        ALLOWED_CHARS_VEHICLE_COLOR
//        getSplCharString(ALLOWED_CHARS_VEHICLE_COLOR)
    }
    val splCharsPassword: String by lazy {
        ALLOWED_CHARS_PASSWORD
//        getSplCharString(ALLOWED_CHARS_PASSWORD)
    }
    val splCharsVehicleRegistration: String by lazy {
        ALLOWED_CHARS_VEHICLE_REGISTRATION_PLATE
//        getSplCharString(ALLOWED_CHARS_VEHICLE_REGISTRATION_PLATE)
    }

    fun countOccurenceOfChar(s: String, c: Char): Int {
        var res = 0
        for (element in s) {
            // checking character in string
            if (element == c) res++
        }
        return res
    }

    fun hiddenEmailText(email: String): String {
        val indexOfAtSymbol = email.indexOf('@')
        val indexOfDot = email.indexOf('.')
        var hiddenEmailText = ""

        email.forEachIndexed { index, c ->
            if (index == 0) {
                hiddenEmailText += c.toString()
            } else if (index > 0) {
                if (index < indexOfAtSymbol || (index > (indexOfAtSymbol + 1) && index < indexOfDot)) {
                    hiddenEmailText += "*"

                } else if ((index == indexOfAtSymbol) || (index == indexOfAtSymbol + 1) || (index >= indexOfDot)) {
                    hiddenEmailText += c.toString()
                }
            }
        }
        return hiddenEmailText
    }

    fun isLastCharOfStringACharacter(input: String): Boolean {
        var isAlphabet = false
        LOWER_CASE.forEach {
            if (input.last().toString().equals(it.toString())) {
                isAlphabet = true
            }
        }
        UPPER_CASE.forEach {
            if (input.last().toString().equals(it.toString())) {
                isAlphabet = true
            }
        }
        return isAlphabet
    }

    fun convertDateForTransferCrossingsScreen(inputDate: String?): String {
        val inputFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
        val date: Date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
        return outputFormat.format(date)
    }

    fun removeAllCharacters(charsToBeRemoved: String, input: String): String {
        var input = input
        charsToBeRemoved.forEach {
            input = input.replace(it.toString(), "")
        }
        return input
    }


//    fun getSplCharString(allowedChars: String): String {
//        var splChar = ""
//        SPECIAL_CHARACTERS.forEach { c ->
//            if (!allowedChars.contains(c.toString())) {
//                splChar = splChar + c
//            }
//        }
//        return splChar
//    }

    //    fun hasSpecialCharacters(str: String, specialCharacterString: String): Boolean {
//        //
//        var hasSpecialChar = false
//        str.forEach { char ->
//            if (specialCharacterString.contains(char.toString())) {
//                hasSpecialChar = true
//                return@forEach
//            }
//        }
//        return hasSpecialChar
//    }
    fun hasSpecialCharacters(input: String, allowedCharacters: String): Boolean {
        var hasSpecialCharacter = false
        input.forEach { inputChar ->
            if (!allowedCharacters.contains(inputChar.toString())) {
                if (!hasAlphabets(inputChar.toString()) && !hasDigits(inputChar.toString()) && inputChar.toString() != " ") {
                    hasSpecialCharacter = true
                }
            }
        }
        return hasSpecialCharacter
    }

    fun hasLowerCase(str: String): Boolean {
        var hasSpecialChar = false
        str.forEach { char ->
            if (LOWER_CASE.contains(char)) {
                hasSpecialChar = true
                return@forEach
            }
        }
        return hasSpecialChar
    }

    fun hasUpperCase(str: String): Boolean {
        var hasSpecialChar = false
        str.forEach { char ->
            if (UPPER_CASE.contains(char)) {
                hasSpecialChar = true
                return@forEach
            }
        }
        return hasSpecialChar
    }

    fun hasDigits(str: String): Boolean {
        var hasDigit = false
        str.forEach { char ->
            if (DIGITS.contains(char)) {
                hasDigit = true
            }
        }
        return hasDigit
    }

    private fun hasAlphabets(str: String): Boolean {
        var hasSpecialChar = false
        str.forEach { char ->
            hasSpecialChar = ALPHABETS.contains(char)
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

    fun redirectToSignoutPage(context: Activity) {
        context.startActivity(
            Intent(context, LandingActivity::class.java).putExtra(
                Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.TYPE, Constants.LOGIN)
        )
    }

    fun sessionExpired(
        context: Activity, listener: LogoutListener? = null, sessionManager: SessionManager
    ) {
        if (sessionManager.getLoggedInUser()) {
            displayCustomMessage(
                context,
                context,
                context.resources.getString(R.string.str_timeout),
                context.getString(R.string.str_for_your_security_account_holder, "2"),
                context.getString(R.string.str_stay_signed_in),
                context.getString(R.string.str_sign_out),
                listener,
                sessionManager
            )

        } else {
            displayCustomMessage(
                context,
                context,
                context.resources.getString(R.string.str_timeout),
                context.getString(R.string.str_for_your_security_non_account_holder, "2"),
                context.getString(R.string.str_stay_on_the_app),
                context.getString(R.string.str_delete_my_answers),
                listener,
                sessionManager
            )

        }
    }

    private fun countDownTimer(
        activity: Activity, sessionManager: SessionManager, message: TextView
    ): CountDownTimer {

        val countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f = DecimalFormat("00") // Two-digit format

                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60

                message.text = activity.resources.getString(
                    R.string.str_for_your_security_account_holder,
                    f.format(min) + ":" + f.format(sec)
                )


            }

            // When the task is over it will print 00:00:00 there
            override fun onFinish() {
                //textView.setText("00:00:00")
                if (sessionManager.getLoggedInUser()) {
                    sessionManager.clearAll()
                    redirectToSignoutPage(activity)
                } else {
                    activity.startNewActivityByClearingStack(LandingActivity::class.java)
                }
            }
        }
        countDownTimer.start()
        return countDownTimer
    }

    fun displayCustomMessage(
        activity: Activity,
        context: Context,
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        listener: LogoutListener? = null,
        sessionManager: SessionManager
    ) {

        val dialog = Dialog(context)
        dialog.setCancelable(false)


        val binding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(context))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)



        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.

        val countDownTimer = countDownTimer(activity, sessionManager, binding.message)
        binding.title.text = fTitle
        binding.message.text = message
        binding.cancelBtn.text = negativeBtnTxt
        binding.okBtn.text = positiveBtnTxt
        binding.cancelBtn.setOnClickListener {
//            nListener?.negativeBtnClick(dialog)
            countDownTimer.cancel()
            if (sessionManager.getLoggedInUser()) {
                sessionManager.clearAll()
                redirectToSignoutPage(activity)
            } else {
                activity.startNewActivityByClearingStack(LandingActivity::class.java)
            }
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
//            pListener?.positiveBtnClick(dialog)
            countDownTimer.cancel()
            LogoutUtil.stopLogoutTimer()
            LogoutUtil.startLogoutTimer(listener)
            dialog.dismiss()
        }
        dialog.show()


    }

    fun displayRetryDialog(
        activity: Activity,
        listener: OnRetryClickListener? = null,
        api_URL: String
    ) {

        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        val binding: DialogRetryBinding = DialogRetryBinding.inflate(LayoutInflater.from(activity))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.retryBtn.setOnClickListener {
            listener?.onRetryClick(api_URL)
            dialog.cancel()
        }
        dialog.show()


    }

    fun displayRetryDialog(
        activity: Context,
        chain: Interceptor.Chain? = null
    ) {

        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        val binding: DialogRetryBinding = DialogRetryBinding.inflate(LayoutInflater.from(activity))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.retryBtn.setOnClickListener {
//            chain.proceed(chain.request())
            dialog.cancel()
        }
        dialog.show()


    }

    fun removeGivenStringCharactersFromString(characterString: String, input: String): String {
        characterString.forEach {
            input.replace(it.toString(), "")
        }
        return input
    }

    fun makeCommaSeperatedStringForPassword(input: String): String {
        var output1 = ""
        input.forEachIndexed { index, c ->
            if (index > 0) {
                output1 = output1 + ", " + c.toString()
            } else {
                output1 = output1 + c.toString()
            }
        }
        if (output1.contains(',')) {
            return output1.substring(0, output1.lastIndexOf(',')) + " and" + output1.substring(
                output1.lastIndexOf(',') + 1,
                output1.length
            )
        } else {
            return output1
        }
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

    fun getManuallyAddedVehicleClass(activity: Activity, vehicleClass: String): String {
        return when (vehicleClass) {
            activity.resources.getString(R.string.vehicle_type_A) -> {
                "A"
            }

            activity.resources.getString(R.string.vehicle_type_B) -> {
                "B"
            }

            activity.resources.getString(R.string.vehicle_type_C) -> {
                "C"
            }

            activity.resources.getString(R.string.vehicle_type_D) -> {
                "D"
            }

            else -> {
                ""
            }
        }
    }

    fun getVehicleType(activity: Activity, vehicleClass: String): String {
        return when (vehicleClass) {
            "A" -> {
                activity.resources.getString(R.string.vehicle_type_A)
            }

            "B" -> {
                activity.resources.getString(R.string.vehicle_type_B)
            }

            "C" -> {
                activity.resources.getString(R.string.vehicle_type_C)
            }

            "D" -> {
                activity.resources.getString(R.string.vehicle_type_D)
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
        if (phoneNumber.isNotEmpty()) {
            val star = phoneNumber.length - 6
            return if (star == 4) {
                phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "*****")

            } else {
                phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "******")

            }
        } else {
            return phoneNumber
        }

    }


    fun getYesterdayDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("dd MMMM yyyy")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }

    fun currentDate(): String {
        return SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(Date())

    }

    fun currentTime(): String {
        return SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())

    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val status = notificationManagerCompat.areNotificationsEnabled()
        return status
    }

    fun getNotificationStatus(context: Context): String {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val status = notificationManagerCompat.areNotificationsEnabled()
        if (status) {
            return "Y"
        } else {
            return "N"
        }
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
                val doublePayment = balance.replace("(", "").replace(")", "")?.toDouble()
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

    fun displaySesionExpiryDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        val binding: DialogSessionexpiryBinding =
            DialogSessionexpiryBinding.inflate(LayoutInflater.from(activity))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.signinTv.setOnClickListener {

            activity.startNewActivityByClearingStack(LoginActivity::class.java)
            dialog.cancel()
        }
        dialog.show()
    }

    fun isStringOnlyInt(input: String): Boolean {
        return input.toBigIntegerOrNull() != null
    }

    fun getCountryCodeRequiredText(text: String) = text.substringAfter('(').replace(")", "")


    fun setMaskWithDots(activity: Activity, info: String?): String {
        return if ((info?.length ?: 0) >= 4) {
            activity.resources.getString(R.string.str_maskcardnumber, info?.takeLast(4))
        } else {
            activity.resources.getString(R.string.str_maskcardnumber, info)
        }

    }

    fun setStarmaskcardnumber(activity: Activity, info: String?): String {
        return if ((info?.length ?: 0) >= 4) {
            activity.resources.getString(R.string.str_starmaskcardnumber, info?.takeLast(4))
        } else {
            activity.resources.getString(R.string.str_starmaskcardnumber, info)
        }

    }

    fun maskCardNumber(cardNumber: String): String {
        return if ((cardNumber.length) >= 4) {
            "****" + cardNumber.takeLast(4)
        } else {
            "****" + cardNumber
        }

//        return cardNumber.replace(cardNumber.substring(0, cardNumber.length - 4), "****")
    }


    fun setCardImage(cardType: String): Int {
        if (cardType.uppercase().equals(Constants.VISA, true)) {
            return R.drawable.visablue
        } else if (cardType.uppercase().equals(Constants.MAESTRO, true)) {
            return R.drawable.maestro
        } else if (cardType.uppercase().equals(Constants.MASTERCARD, true)) {
            return R.drawable.mastercard
        } else if (cardType.uppercase().equals(Constants.CURRENT, true)) {
            return R.drawable.directdebit
        } else {
            return R.color.white
        }
    }

    fun redirectToNotificationPermissionSettings(context: Context) {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= VERSION_CODES.O -> {
                    // For Android 8.0 (API level 26) and above
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }

                else -> {
                    // For Android 4.4 and below
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", context.packageName)
                    putExtra("app_uid", context.applicationInfo.uid)
                }
            }
        }
        context.startActivity(intent)

    }
    fun isRooted(context: Context): Boolean {
        return CommonUtils.isRooted() || isRooted2() || isEmulator(context)
    }

    fun isEmulator(context: Context): Boolean {
        val androidId: String = Settings.Secure.getString(context.contentResolver, "android_id")
        return "sdk" == Build.PRODUCT || "google_sdk" == Build.PRODUCT || androidId == null
    }
    private fun isRooted2(): Boolean {
        return findBinary("su")
    }
    fun findBinary(binaryName: String): Boolean {
        var found = false
        if (!found) {
            val places = arrayOf(
                "/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/",
                "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"
            )
            for (where in places) {
                if (File(where + binaryName).exists()) {
                    found = true
                    break
                }
            }
        }
        return found
    }

}