package com.conduent.nationalhighways.utils.common

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.databinding.DialogSessionexpiryBinding
import com.conduent.nationalhighways.receiver.ForegroundServiceWorker
import com.conduent.nationalhighways.service.PlayLocationService
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import com.conduent.nationalhighways.utils.rating.RatingDialog
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import com.conduent.nationalhighways.utils.widgets.NHTextInputCell
import com.google.firebase.crashlytics.internal.common.CommonUtils
import java.io.File
import java.lang.reflect.Field
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {
    lateinit var progressBar: Dialog

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


    var LOWER_CASE = "qwertyuiopasdfghjklzxcvbnm"
    var UPPER_CASE = "QWERTYUIOPASDFGHJKLZXCVBNM"
    var UK_MOBILE_REGEX: Regex = Regex("[0]{0,1}7[0-9]{9}")
    var ACCOUNT_NAME_FIRSTNAME_LASTNAME: Regex = Regex("[a-zA-Z\\-]+")
    var PHONENUMBER: Regex = Regex("[0]{0,3}[1-9]{1}[0-9]{7,14}")
    var NUMBER: Regex = Regex(".*[0-9]+.*")
    var UPPERCASE: Regex = Regex(".*[A-Z]+.*")
    var TWO_OR_MORE_DOTS: Regex = Regex("[\\.]+[\\.]+")
    var LOWECASE: Regex = Regex(".*[a-z]+.*")
    var DIGITS = "0123456789"
    private var ALPHABETS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
    val splCharAddress1: String by lazy {
        ALLOWED_CHARS_BUILDING_STREE_NO
    }

    val splCharAddress2: String by lazy {
        ALLOWED_CHARS_ADDRESS_LINE_2
    }

    val splCharTownCity: String by lazy {
        ALLOWED_CHARS_TOWN_OR_CITY
    }

    val splCharPostCode: String by lazy {
        ALLOWED_CHARS_POSTCODE
    }

    fun capitalizeString(str: String?): String? {
        return if (str.isNullOrEmpty()) {
            str
        } else {
            str.split("\\s+".toRegex()).joinToString(" ") { word ->
                word.split("-").joinToString("-") { part ->
                    part.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
        }
    }


    private fun smsSupportCountryList(): ArrayList<String> {
        val supportCountryList = ArrayList<String>()

        supportCountryList.add("Austria - (+43)")
        supportCountryList.add("Bosnia and Herzegovina - (+387)")
        supportCountryList.add("Bulgaria - (+359)")
        supportCountryList.add("Croatia - (+385)")
        supportCountryList.add("Cyprus - (+357)")
        supportCountryList.add("Czech Republic - (+420)")
        supportCountryList.add("Denmark - (+45)")
        supportCountryList.add("Estonia - (+372)")
        supportCountryList.add("Finland - (+358)")
        supportCountryList.add("France - (+33)")
        supportCountryList.add("Germany - (+49)")
        supportCountryList.add("Gibraltar - (+350)")
        supportCountryList.add("Greece - (+30)")
        supportCountryList.add("Ireland - (+353)")
        supportCountryList.add("Italy - (+39)")
        supportCountryList.add("Latvia -  (+371)")
        supportCountryList.add("Lithuania -  (+370)")
        supportCountryList.add("Luxembourg -  (+352)")
        supportCountryList.add("Macedonia, The Former Yuogoslav - (+389)")
        supportCountryList.add("Malta -  (+356)")
        supportCountryList.add("Moldova (Republic of) -  (+373)")
        supportCountryList.add("Netherlands -  (+31)")
        supportCountryList.add("Norway -  (+47)")
        supportCountryList.add("Poland - (+48)")
        supportCountryList.add("Portugal -  (+351)")
        supportCountryList.add("Slovakia -  (+421)")
        supportCountryList.add("Slovenia -  (+386)")
        supportCountryList.add("Spain -  (+34)")
        supportCountryList.add("Sweden -  (+46)")
        supportCountryList.add("Switzerland -  (+41)")
        supportCountryList.add("Ukraine -  (+380)")
        supportCountryList.add("United Kingdom - (+44)")
        supportCountryList.add("Romania - (+40)")

        return supportCountryList
    }

    fun sortTransactionsDateWiseDescending(transactions: MutableList<TransactionData>): MutableList<TransactionData> {

        transactions.sortWith(Comparator { o1, o2 ->
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            var date1: Date? = null
            var date2: Date? = null
            try {
                date1 = dateFormat.parse(o1.transactionDate.toString())
                date2 = dateFormat.parse(o2.transactionDate.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (date1 != null && date2 != null) {
                return@Comparator date2.compareTo(date1)
            }
            return@Comparator 0
        })
        return transactions
    }

    fun sortAlertsDateWiseDescending(transactions: MutableList<AlertMessage>): MutableList<AlertMessage> {
        val transactionListSorted: MutableList<AlertMessage> = mutableListOf()
        for (transaction in transactions) {
            if (transactionListSorted.isEmpty()) {
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
            if (mText.length == 1 && mText == ".") {
                mText = "0"
            }
            val amount: String = if (isDecimal(minimumAmount)) {
                minimumAmount.toString()
            } else {
                minimumAmount.toInt().toString()
            }

            isValid = if (mText.toDouble().toInt() <= 100000) {

                if (mText.toDouble() < minimumAmount) {
                    if (isTopUp) {
                        nhTextInputCell.setErrorText(
                            nhTextInputCell.context.getString(
                                R.string.str_top_up_amount_must_be_more,
                                amount
                            )
                        )
                    } else {
                        nhTextInputCell.setErrorText(
                            nhTextInputCell.context.getString(
                                R.string.str_low_balance_must_be_more,
                                amount
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

    fun isDecimal(num: Double): Boolean {
        return num % 1 != 0.0
    }

    val splCharEmailCode: String by lazy {
        ALLOWED_CHARS_EMAIL
    }
    val splCharCompanyName: String by lazy {
        ALLOWED_CHARS_COMPANY_NAME
    }
    val splCharVehicleMake: String by lazy {
        ALLOWED_CHARS_VEHICLE_MAKE
    }
    val splCharVehicleModel: String by lazy {
        ALLOWED_CHARS_VEHICLE_MODEL
    }
    val splCharVehicleColor: String by lazy {
        ALLOWED_CHARS_VEHICLE_COLOR
    }
    val splCharsPassword: String by lazy {
        ALLOWED_CHARS_PASSWORD
    }
    val splCharsVehicleRegistration: String by lazy {
        ALLOWED_CHARS_VEHICLE_REGISTRATION_PLATE
    }

    fun countOccurrenceOfChar(s: String, c: Char): Int {
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
            if (input.last().toString() == it.toString()) {
                isAlphabet = true
            }
        }
        UPPER_CASE.forEach {
            if (input.last().toString() == it.toString()) {
                isAlphabet = true
            }
        }
        return isAlphabet
    }

    fun convertDateForTransferCrossingsScreen(inputDate: String?): String {
        val inputFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
        val date: Date? = inputFormat.parse(inputDate.toString())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return outputFormat.format(date ?: Date())
    }

    fun convertOneFormatDateToAnotherFormat(
        inputDate: String?,
        fromFormat: String,
        anotherFormat: String
    ): String {
        val inputFormat = SimpleDateFormat(fromFormat, Locale.US)
        val date: Date? = inputFormat.parse(inputDate.toString())
        val outputFormat = SimpleDateFormat(anotherFormat, Locale.US)
        return outputFormat.format(date ?: Date())
    }

    fun removeAllCharacters(charsToBeRemoved: String, input: String): String {
        var input1 = input
        charsToBeRemoved.forEach {
            input1 = input.replace(it.toString(), "")
        }
        return input1
    }

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
        Log.e("TAG", "hasLowerCase: str " + str)
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
        Log.e("TAG", "hasUpperCase: str " + str)
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
        Log.e("TAG", "hasDigits: str " + str)
        var hasDigit = false
        str.forEach { char ->
            if (DIGITS.contains(char)) {
                hasDigit = true
            }
        }
        return hasDigit
    }

    private fun hasAlphabets(str: String): Boolean {
        Log.e("TAG", "hasAlphabets: str " + str)
        var hasSpecialChar = false
        str.forEach { char ->
            hasSpecialChar = ALPHABETS.contains(char)
        }
        return hasSpecialChar
    }

    var specialCharacter: Regex = Regex("[0-9$&+,:;=\\\\?@#|/'<>.^*()%!]")

    fun redirectToSignOutPage(context: Activity) {
        context.startActivity(
            Intent(context, LandingActivity::class.java).putExtra(
                Constants.SHOW_SCREEN, Constants.SESSION_TIME_OUT
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.TYPE, Constants.LOGIN)
        )
    }

    fun sessionExpired(
        context: Activity,
        listener: LogoutListener? = null,
        sessionManager: SessionManager,
        apiService: ApiService
    ) {
        if (context is HomeActivityMain) {
            displayCustomMessage(
                context,
                context,
                context.resources.getString(R.string.str_timeout),
                context.getString(R.string.str_for_your_security_account_holder, "2"),
                context.getString(R.string.str_stay_signed_in),
                context.getString(R.string.str_sign_out),
                listener,
                sessionManager, apiService, true
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
                sessionManager, apiService, false
            )

        }
    }

    private fun countDownTimer(
        activity: Activity, sessionManager: SessionManager, message: TextView, loggedInUser: Boolean
    ): CountDownTimer {

        val countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f = DecimalFormat("00") // Two-digit format

                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60

                if (loggedInUser) {
                    message.text = activity.resources.getString(
                        R.string.str_for_your_security_account_holder,
                        f.format(min) + ":" + f.format(sec)
                    )

                } else {
                    message.text = activity.resources.getString(
                        R.string.str_for_your_security_non_account_holder,
                        f.format(min) + ":" + f.format(sec)
                    )

                }


            }

            // When the task is over it will print 00:00:00 there
            override fun onFinish() {
                //textView.setText("00:00:00")
                if (sessionManager.getLoggedInUser()) {
                    sessionManager.clearAll()
                    redirectToSignOutPage(activity)
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
        sessionManager: SessionManager,
        apiService: ApiService,
        loggedInUser: Boolean = false
    ) {
        Log.e(
            "TAG",
            "displayCustomMessage() called with: activity = $activity, context = $context, fTitle = $fTitle, message = $message, positiveBtnTxt = $positiveBtnTxt, negativeBtnTxt = $negativeBtnTxt, listener = $listener, sessionManager = $sessionManager, apiService = $apiService"
        )
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

        val countDownTimer = countDownTimer(activity, sessionManager, binding.message, loggedInUser)
        binding.title.text = fTitle
        binding.message.text = message
        binding.cancelBtn.text = negativeBtnTxt
        binding.okBtn.text = positiveBtnTxt
        binding.cancelBtn.setOnClickListener {
//            nListener?.negativeBtnClick(dialog)
            countDownTimer.cancel()
            if (activity is HomeActivityMain) {
                sessionManager.clearAll()
                redirectToSignOutPage(activity)
            } else {
                activity.startNewActivityByClearingStack(LandingActivity::class.java)
            }
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
//            pListener?.positiveBtnClick(dialog)

            BaseApplication.getNewToken(api = apiService, sessionManager)

            countDownTimer.cancel()
            LogoutUtil.stopLogoutTimer()
            LogoutUtil.startLogoutTimer(listener)
            dialog.dismiss()
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
        var textInput = input
        textInput = textInput.toSet().joinToString("")

        var output1 = ""
        textInput.forEachIndexed { index, c ->
            output1 = if (index > 0) {
                "$output1, $c"
            } else {
                output1 + c.toString()
            }
        }
        return if (output1.contains(',')) {
            output1.substring(0, output1.lastIndexOf(',')) + " and" + output1.substring(
                output1.lastIndexOf(',') + 1,
                output1.length
            )
        } else {
            output1
        }
    }

    fun isValidPassword(password: String?): Boolean {
        val PASSWORD_PATTERN = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z]).*$"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password.toString())
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
                vehicleClass
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
                vehicleClass
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
                "2"
            }
        }
    }


    fun mobileNumber(mobNo: String?): String {
        val regexPattern =
            "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
        val match: Matcher
        val pattern = Pattern.compile(regexPattern)
        match = pattern.matcher(mobNo.toString())
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
        if (email.isEmpty()) {
            return email
        }
        val arrEmail = email.split("@")
        val mailbox = arrEmail[0].subSequence(0, 1).toString() + "*".repeat(arrEmail[0].length - 1)

        val domain = arrEmail[1].split(".")[0]

        val maskedDomain = domain.subSequence(0, 1).toString() + "*".repeat(domain.length - 1)


        return mailbox + "@" + maskedDomain + arrEmail[1].replace(domain, "")
    }

    fun maskPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.isNotEmpty()) {
            val star = phoneNumber.length - 6
            if (star == 4) {
                phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "*****")
            } else {
                phoneNumber.replace(phoneNumber.substring(2, phoneNumber.length - 3), "******")
            }
        } else {
            phoneNumber
        }

    }

    fun currentDateWithTimeTime(): String {
        return SimpleDateFormat("hh:mma 'on' dd MMM yyyy", Locale.getDefault()).format(Date())
            .replace("AM", "am")
            .replace("PM", "pm")

    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        return notificationManagerCompat.areNotificationsEnabled()
    }

    fun getNotificationStatus(context: Context): String {
        return "N"
        /*val notificationManagerCompat = NotificationManagerCompat.from(context)
        val status = notificationManagerCompat.areNotificationsEnabled()
        return if (status) {
            "Y"
        } else {
            "N"
        }*/
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

    fun convertToPoundFormat(currentBalance: String?): CharSequence? {
        var balance = currentBalance
        if (balance.isNullOrEmpty()) {
            return "£ 0.00"
        }
        try {
            balance = currentBalance?.replace("£", "")?.replace(",", "")
            val number = NumberFormat.getCurrencyInstance(Locale.UK)

            return if (balance?.contains("(") == true && balance.contains(")")) {
                val doublePayment = balance.replace("(", "").replace(")", "").toDouble()
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

    fun setStarmaskcardnumber(activity: Activity, info: String?): String {
        return if ((info?.length ?: 0) >= 4) {
            activity.resources.getString(R.string.str_starmaskcardnumber, info?.takeLast(4))
        } else {
            activity.resources.getString(R.string.str_starmaskcardnumber, info)
        }

    }

    fun maskSixteenDigitCardNumber(cardNumber: String): String {
        return if ((cardNumber.length) >= 4) {
            "************" + cardNumber.takeLast(4)
        } else {
            "************$cardNumber"
        }
    }


    fun maskCardNumber(cardNumber: String): String {
        return if ((cardNumber.length) >= 4) {
            "****" + cardNumber.takeLast(4)
        } else {
            "****$cardNumber"
        }

    }

    fun setCardImage(paymentTypeInfo: String): Int {
        return if (paymentTypeInfo.uppercase().contains("CURRENT")) {
            R.drawable.directdebit
        } else if (paymentTypeInfo.uppercase().contains("SAVINGS")) {
            R.drawable.directdebit
        } else if (paymentTypeInfo.uppercase().contains("MASTERCARD")) {
            R.drawable.mastercard
        } else if (paymentTypeInfo.uppercase().contains(Constants.MAESTRO)) {
            R.drawable.maestro
        } else if (paymentTypeInfo.uppercase().contains("VISA")) {
            R.drawable.visablue
        } else {
            R.color.white
        }
    }

    fun returnCardText(paymentTypeInfo: String): String {
        return if (paymentTypeInfo.contains("CURRENT")) {
            "CURRENT"
        } else if (paymentTypeInfo.contains("SAVINGS")) {
            "SAVINGS"
        } else if (paymentTypeInfo.contains("MASTERCARD")) {
            "MASTERCARD"
        } else if (paymentTypeInfo.contains(Constants.MAESTRO)) {
            "MAESTRO"
        } else if (paymentTypeInfo.contains("VISA")) {
            "VISA"
        } else {
            ""
        }
    }

    fun redirectToNotificationPermissionSettings(context: Context) {
        val intent = Intent().apply {
            // For Android 8.0 (API level 26) and above
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)

    }

    fun isRooted(context: Context): Boolean {
        return CommonUtils.isRooted() || isRooted2() || isEmulator(context)
    }

    private fun isEmulator(context: Context): Boolean {
        val androidId: String = Settings.Secure.getString(context.contentResolver, "android_id")
        return "sdk" == Build.PRODUCT || "google_sdk" == Build.PRODUCT || androidId == null
    }

    private fun isRooted2(): Boolean {
        return findBinary("su")
    }

    private fun findBinary(binaryName: String): Boolean {
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

    fun convertStringToDate(dateString: String, dateFormat: String): Date? {
        val dateFormat1 = SimpleDateFormat(dateFormat, Locale.getDefault())
        return try {
            dateFormat1.parse(dateString)
        } catch (e: Exception) {
            Date()
        }
    }

    fun convertStringToDate1(dateString: String, dateFormat: String): Date? {
        val dateFormat1 = SimpleDateFormat(dateFormat, Locale.getDefault())
        return try {
            dateFormat1.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun getTimeDifference(startTime: Date, endTime: Date): Triple<Long, Long, Long> {
        return try {
            val differenceInMillis = endTime.time - startTime.time
            val hours = differenceInMillis / (1000 * 60 * 60)
            val minutes = differenceInMillis % (1000 * 60 * 60) / (1000 * 60)

            val millisecondsInDay = 1000L * 60 * 60 * 24
            val days = differenceInMillis / millisecondsInDay

            Log.e("TAG", "getTimeDifference:hours "+hours+" *minutes* "+minutes )
//            Triple(hours, minutes, months)
            Triple(hours, minutes, days)
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }

    }

    fun getMinSecTimeDifference(startTime: Date, endTime: Date): Pair<Long, Long> {
        return try {
            val differenceInMillis = endTime.time - startTime.time
            val minutes = differenceInMillis % (1000 * 60 * 60) / (1000 * 60)
            val seconds = differenceInMillis % (1000 * 60) / 1000

            Pair(minutes, seconds)
        } catch (e: Exception) {
            Pair(0, 0)
        }

    }

    fun hasFaceId(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
    }

    fun hasTouchId(context: Context): Boolean {


        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)

    }


    fun validationsToShowRatingDialog(activity: Activity, sessionManager: SessionManager) {
        if (sessionManager.fetchStringData(SessionManager.LAST_RATING_TIME).isEmpty()) {
            showRatingDialog(activity, sessionManager)
        } else {
            val lastTokenTime = convertStringToDate(
                sessionManager.fetchStringData(SessionManager.LAST_RATING_TIME),
                Constants.dd_mm_yyyy_hh_mm_ss
            )
            if (lastTokenTime != null) {
                val diff = getTimeDifference(lastTokenTime, Date())
                if (diff.third >= 1) {
                    showRatingDialog(activity, sessionManager)
                }
            }
        }


    }

    fun showRatingDialog(activity: Activity, sessionManager: SessionManager) {
        val dateFormat = SimpleDateFormat(Constants.dd_mm_yyyy_hh_mm_ss, Locale.getDefault())
        val dateString = dateFormat.format(Date())
        sessionManager.saveStringData(SessionManager.LAST_RATING_TIME, dateString)

        val ratingDialog = RatingDialog.Builder(activity)
            .icon(R.mipmap.appicon)
//            .session(3)
//            .threshold(3)
            .ratingBarColor(R.color.blue_color)
            .playstoreUrl("https://play.google.com/store/apps/details?id=com.doctormoney")
            .onThresholdCleared { dialog, rating, thresholdCleared ->
                Log.e(
                    "TAG",
                    "onThresholdCleared: $dialog $rating $thresholdCleared"
                )
            }
            .onThresholdFailed { dialog, rating, thresholdCleared ->
                Log.e(
                    "TAG",
                    "onThresholdFailed: $dialog $rating $thresholdCleared"
                )
            }
            .onRatingChanged { rating, thresholdCleared ->
                Log.e(
                    "TAG",
                    "onRatingChanged: $rating $thresholdCleared"
                )
            }
            .onRatingBarFormSubmit { feedback ->
                Log.e(
                    "TAG",
                    "onRatingBarFormSubmit: $feedback"
                )
            }
            .build()
        ratingDialog.window?.setBackgroundDrawableResource(android.R.color.white) // Change to your desired color

        ratingDialog.show()


    }

    fun vibrate(activity: Activity) {
        val vibrator: Vibrator =
            activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Create a VibrationEffect object for the default vibration strength
        val vibrationEffect =
            VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        // Vibrate the device with the created VibrationEffect
        vibrator.vibrate(vibrationEffect)
    }

    fun showProgressBar(context: Context, isProgressVisibile: Boolean) {
        try {
            if (isProgressVisibile) {
                if (this::progressBar.isInitialized) {
                    progressBar.dismiss()
                }
            }
            if (isProgressVisibile) {
                progressBar = Dialog(context)
                progressBar.setContentView(R.layout.dialog_loader)
                progressBar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                progressBar.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                progressBar.setCancelable(false)
                progressBar.show()
            } else {
                if (this::progressBar.isInitialized) {
                    progressBar.dismiss()
                }
            }
        } catch (_: Exception) {

        }
    }


    fun onBackPressed(context: Context) {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intent)
    }


    fun getFileExtension(filePath: String): String {
        Log.e("TAG", "getFileExtension: filePath " + filePath)
        /* if (filePath.length >= 3) {
             return filePath.substring(filePath.length - 3)
         } else {
             return filePath
         }*/

        return if (filePath.contains(".")) {
            filePath.substring(filePath.lastIndexOf(".") + 1)
        } else {
            ""
        }
    }

    fun checkFileTypeByExtension(filePath: String): Boolean {
        val extension = getFileExtension(filePath)
        return extension.equals("jpg", ignoreCase = true) ||
                extension.equals("pdf", ignoreCase = true) ||
                extension.equals("bmp", ignoreCase = true) ||
                extension.equals("tiff", ignoreCase = true)
    }

    fun returnSharedPreference(context: Context): SharedPreferences {
        val masterAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "HE_MOBILE",
            masterAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun hasSameExtensionTwice(input: String): Boolean {
        val parts = input.split('.')
        return parts.size >= 3 && parts[parts.size - 2] == parts[parts.size - 1]
    }


    fun removeLastExtension(input: String, extension: String): String {
        Log.e(
            "TAG",
            "removeLastExtension() called with: input = $input, extension = $extension"
        )
        val lastIndexOfExtension = input.lastIndexOf(extension)
        return if (lastIndexOfExtension != -1) {
            input.substring(
                0,
                lastIndexOfExtension
            ) + input.substring(lastIndexOfExtension + extension.length)
        } else {
            input // Return the original string if the extension is not found
        }
    }

    fun returnMfaStatus(mfa: String): String {
        var mfaEnabled = "Y"
        if (mfa == "false") {
            mfaEnabled = "N"
        }
        return mfaEnabled
    }

    fun returnEditProfileModel(
        businessName: String? = null,
        fein: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        addressLine1: String? = null,
        addressLine2: String? = null,
        city: String? = null,
        state: String? = null,
        zipCode: String? = null,
        zipCodePlus: String? = null,
        country: String? = null,
        emailAddress: String? = null,
        primaryEmailStatus: String? = null,
        primaryEmailUniqueID: String? = null,
        phoneCell: String? = null,
        phoneCellCountryCode: String? = null,
        phoneDay: String? = null,
        phoneDayCountryCode: String? = null,
        phoneFax: String? = null,
        smsOption: String? = null,
        phoneEvening: String? = null,
        correspDeliveryMode: String? = null,
        correspDeliveryFrequency: String? = null,
        mfaEnabled: String? = null,
        accountType: String? = null,
        securityCode: String? = null,
        referenceId: String? = null,
    ): UpdateProfileRequest {
        var correspondDeliveryModeDup = correspDeliveryMode
        var correspondDeliveryFrequencyDup = correspDeliveryFrequency
        var businessNameDup: String? = null

        if (accountType.equals(
                Constants.BUSINESS_ACCOUNT,
                true
            )
        ) {
            businessNameDup = businessName ?: ""
        }
        if (correspDeliveryMode == null) {
            correspondDeliveryModeDup = ""
        }
        if (correspDeliveryFrequency == null) {
            correspondDeliveryFrequencyDup = ""
        }
        return UpdateProfileRequest(
            businessNameDup,
            null,
            firstName,
            lastName,
            addressLine1,
            addressLine2,
            city,
            state,
            zipCode,
            zipCodePlus,
            country,
            emailAddress,
            primaryEmailStatus,
            primaryEmailUniqueID,
            phoneCell,
            phoneCellCountryCode,
            phoneDay,
            phoneDayCountryCode,
            phoneFax,
            smsOption,
            phoneEvening,
            correspondDeliveryModeDup,
            correspondDeliveryFrequencyDup,
            mfaEnabled,
            securityCode = securityCode,
            referenceId = referenceId
        )
    }

    fun getCountryName(sessionManager: SessionManager, countryCode: String): String {
        val countries = sessionManager.fetchStringData(SessionManager.COUNTRIES)


        val countriesList = ArrayList<CountriesModel>()

        val pattern =
            """CountriesModel\(id=(\d+), countryCode=(\w+), countryName=([\w\s]+)\)""".toRegex()
        pattern.findAll(countries).forEach { matchResult ->
            val (id, countryCode, countryName) = matchResult.destructured
            countriesList.add(CountriesModel(id, countryCode, countryName))
        }

        val filteredList = countriesList.filter { it.countryCode == countryCode }
        return if (filteredList.isNotEmpty()) {
            filteredList[0].countryName ?: ""
        } else {
            ""
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PERMISSION_DENIED

    }

    fun checkAccessFineLocationPermission(context: Context): Boolean {
        return !((ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PERMISSION_DENIED))

    }

    fun isSupportedCountry(country: String): Boolean {
        if (country.isEmpty()) {
            return true
        }
        val smsSupportCountryList = smsSupportCountryList().map {
            it.trim()
                .replace(" ", "")
                .replace("-", "").lowercase()
            extractTextWithinBrackets(it)
        }

        return smsSupportCountryList.contains(
            extractTextWithinBrackets(country).trim()
                .replace(" ", "").replace("-", "").lowercase()
        )
    }

    fun extractTextWithinBrackets(input: String): String {
        val startIndex = input.indexOf("(")
        val endIndex = input.indexOf(")")

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            input.substring(startIndex + 1, endIndex)
        } else {
            input
        }
    }

    fun openAppSettings(activity: Activity) {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = activity.packageName // Replace with your app's package name
        val appSettingsUri = Uri.fromParts("package", packageName, null)
        appSettingsIntent.data = appSettingsUri
        activity.startActivity(appSettingsIntent)
    }

    fun replaceAsterisks(input: String): String {

        var count = 0
        val stringBuilder = StringBuilder()
        var previousChar: Char? = null
        input.forEachIndexed { index, char ->
            var nextChar = ""
            if (input.length > (index + 1)) {
                nextChar = input[index + 1].toString()
            }

            if (char == '*') {
                if (previousChar == '*' && count != 0 && nextChar != "*") {
                    count++
                    stringBuilder.append("" + count.toString() + "asterisk")
                    count = 0
                } else {
                    count++
                }
            } else {
                if (count != 0) {
                    stringBuilder.append("" + count.toString() + "asterisk")
                    count = 0
                }
                stringBuilder.append(char)
            }
            previousChar = char
        }
        return stringBuilder.toString()
    }

    fun setupAccessibilityDelegatesForRadioButtons(radioGroup: RadioGroup) {
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.setAccessibilityDelegate()
            }
        }
    }


    fun accessibilityForNumbers(value: String): StringBuilder {
        val builder = StringBuilder()
        var isFirstDigit = true

        for (data in value) {
            if (data.isDigit()) {
                if (!isFirstDigit) {
                    builder.append("\u202F") // Use a narrow no-break space
                }
                builder.append(data)
                isFirstDigit = false
            } else {
                builder.append(data)
            }
        }
        return builder
    }

    fun writeInFile(context: Context?, message: String) {
        try {
            /*   val directory =
                   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
               val file = File(directory, "dartlogs.txt")
               if (file.exists()) {
                   val fileWriter = FileWriter(file, true)
                   val bufferedWriter = BufferedWriter(fileWriter)

                   bufferedWriter.write(message + "\n")
                   bufferedWriter.newLine()
                   bufferedWriter.close()
               }*/
        } catch (e: Exception) {

        }
    }

    fun startLocationService(activity: Context) {
        Log.e("TAG", "startLocationService:  ")
        if (isLocationServiceRunning(activity)) {
            Log.e("TAG", "startLocationService: Running ")
        } else {
            Log.e("TAG", "startLocationService: Not Running ")
            try {
                val periodicWorkRequest = PeriodicWorkRequest.Builder(
                    ForegroundServiceWorker::class.java,
                    1, TimeUnit.HOURS // Interval for starting the foreground service
                ).build()

                WorkManager.getInstance(activity).enqueue(periodicWorkRequest)

                val i = Intent(activity, PlayLocationService::class.java)
                Log.e("TAG", "startForegroundService: Running-- ")
                activity.startForegroundService(i)
            } catch (e: Exception) {
                writeInFile(
                    activity,
                    "-------- StartLocation Service exception --------${e.message} "
                )
            }
        }
    }

    fun isLocationServiceRunning(activity: Context): Boolean {
        val manager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            Log.e("TAG", "isLocationServiceRunning: " + service.service.className)
            if ("com.conduent.nationalhighways.service.PlayLocationService" == service.service.className) {
                return true
            }
        }
        return false
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }


    fun checkNullValuesOfModel(model: CardListResponseModel?) {
        if (model?.check == null) {
            model?.check = false
        }
        if (model?.isSelected == null) {
            model?.isSelected = false
        }
        if (model?.bankRoutingNumber == null) {
            model?.bankRoutingNumber = ""
        }
        if (model?.cardType == null) {
            model?.cardType = ""
        }
        if (model?.cardNumber == null) {
            model?.cardNumber = ""
        }
        if (model?.middleName == null) {
            model?.middleName = ""
        }
        if (model?.expMonth == null) {
            model?.expMonth = ""
        }
        if (model?.expYear == null) {
            model?.expYear = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }

        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.rowId == null) {
            model?.rowId = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.firstName == null) {
            model?.firstName = ""
        }
        if (model?.lastName == null) {
            model?.lastName = ""
        }
        if (model?.customerVaultId == null) {
            model?.customerVaultId = ""
        }
        if (model?.addressLine1 == null) {
            model?.addressLine1 = ""
        }
        if (model?.city == null) {
            model?.city = ""
        }
        if (model?.state == null) {
            model?.state = ""
        }
        if (model?.zipCode == null) {
            model?.zipCode = ""
        }
        if (model?.country == null) {
            model?.country = ""
        }
        if (model?.emandateStatus == null) {
            model?.emandateStatus = ""
        }
        if (model?.paymentSeqNumber == null) {
            model?.paymentSeqNumber = 0
        }
        if (model?.bankAccount == null) {
            model?.bankAccount = false
        }
    }


    fun checkLastLoggedInEmail(sessionManager: SessionManager, email: String): Boolean {
        val lastLoggedInEmail =
            sessionManager.fetchStringData(SessionManager.LAST_LOGGEDIN_EMAIL)
        return email == lastLoggedInEmail
    }

    fun checkReValidationPayment(
        paymentList: MutableList<CardListResponseModel?>?,
        accountInformation: AccountInformation?
    ): Pair<Boolean, ArrayList<CardListResponseModel>?> {
        if (paymentList?.isNotEmpty() == true) {
            val cardResult: ArrayList<CardListResponseModel> =
                paymentList.filter { it?.isValidated == false && it.bankAccount == false } as ArrayList<CardListResponseModel>

            var paymentCard: CardListResponseModel? = null
            cardResult.forEach { card ->
                if (card.primaryCard == true) {
                    paymentCard = card
                }
            }

            return if (cardResult.isNotEmpty()) {
                if (accountInformation?.accSubType == "PAYG") {
                    if (paymentCard != null) {
                        Pair(paymentCard?.primaryCard == true, cardResult)
                    } else {
                        Pair(false, null)
                    }
                } else {
                    Pair((paymentCard != null), cardResult)
                }
            } else {
                Pair(false, null)
            }


        } else {
            return Pair(false, null)

        }

    }

    fun checkCrossedInTime(): Boolean {
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Get the current hour and minute
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if ((hour >= 6 && minute >= 2) && (hour <= 21 && minute <= 58)) {
            return true
        }
        return false
    }

    fun getDelayHours(sessionManager: SessionManager, context: Context): Long {
        val type=sessionManager.fetchStringData(SessionManager.DAILY_REMINDER_TYPE)


        when (type) {
            context.resources.getString(R.string.str_1hr_after_crossing) -> {
                return 1
            }
            context.resources.getString(R.string.str_2hr_after_crossing) -> {
                return 2
            }
            context.resources.getString(R.string.str_3hr_after_crossing) -> {
                return 3
            }
            context.resources.getString(R.string.str_4hr_after_crossing) -> {
                return 4
            }
            context.resources.getString(R.string.str_5hr_after_crossing) -> {
                return 5
            }
            context.resources.getString(R.string.str_6hr_after_crossing) -> {
                return 6
            }
            context.resources.getString(R.string.str_7hr_after_crossing) -> {
                return 7
            }
            context.resources.getString(R.string.str_8hr_after_crossing) -> {
                return 8
            }
            else -> return 0
        }
    }
}
