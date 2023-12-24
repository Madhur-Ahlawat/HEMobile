package com.conduent.nationalhighways.utils.common

import android.content.SharedPreferences
import com.google.gson.Gson
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session manager to save and fetch data from SharedPreferences
 */

@Singleton
class SessionManager @Inject constructor(private val prefs: SharedPreferences) {
    /*private var prefs: SharedPreferences =
        context.getSharedPreferences("HE_MOBILE", Context.MODE_PRIVATE)*/


    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_TOKEN_TIME_OUT = "user_token_time_out"
        const val Refresh_TOKEN = "refresh_token"
        const val ACCOUNT_NUMBER = "account_number"
        const val SMS_OPTION = "sms_option"
        const val NOTIFICATION_OPTION = "notification_option"
        const val ACCOUNT_STATUS = "account_status"
        const val ACCOUNT_TYPE = "account_type"
        const val SUB_ACCOUNT_TYPE = "sub_account_type"
        const val USER_EMAIL_ID = "user_email_id"
        const val SECURITY_CODE = "security_code"
        const val SESSION_TIME = "session_time"
        const val SECURITY_CODE_OBJ = "security_code_obj"
        const val IS_USER_LOGIN = "is_user_login"
        const val IS_SECONDARY = "is_secondary_user"
        const val LOGGED_IN_USER = "logged_in_user"
        const val NC_ID = "nc_id"
        const val PUSH_TOKEN = "firebase_notification_token"
        const val USER_NAME = "username"
        const val USER_COUNTRYCODE = "usercountrycode"
        const val USER_MOBILENUMBER = "usermobilenumber"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val ZIPCODE = "zipcode"
        const val PHONE_NUMBER = "phoneNumber"

        val BIOMETRICTOKEN: String = "ACSInrixTrafficApp"
        val TOUCH_ID_ENABLED: String = "touch_ID"
        val TOUCH_ID_USER_ID: String = "TOUCH_ID_USER_ID"
        val PRIVATE_KEY: String = ""
        val PUBLIC_KEY: String = ""
        val CATEGORIES_DATA="categories_data"
        val SUB_CATEGORIES_DATA="sub_categories_data"
        val LAST_TOKEN_TIME="last_token_time"
        val LAST_RATING_TIME="last_rating_time"

        val GEOFENCE_ENTER_TIME="geofence_enter_time"
        val COUNTRIES="COUNTRIES"
        val LOCATION_PERMISSION="LOCATION_PERMISSION"
        val NOTIFICATION_PERMISSION="NOTIFICATION_PERMISSION"
    }

    /**
     * Function to save time out
     */
    fun saveAuthTokenTimeOut(time: Int?) {
        time?.let {
            prefs.edit().apply {
                putInt(USER_TOKEN_TIME_OUT, time)
            }.apply()
        }
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthTokenTimeout(): Int {
        return prefs.getInt(USER_TOKEN_TIME_OUT, 0)
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String?) {
        prefs.edit().apply {
            putString(USER_TOKEN, token)
        }.apply()
    }

    /**
     * Function to save auth token
     */
    fun saveRefreshToken(token: String) {
        prefs.edit().apply {
            putString(Refresh_TOKEN, token)
        }.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Function to fetch refresh token
     */
    fun fetchRefreshToken(): String? {
        return prefs.getString(Refresh_TOKEN, null)
    }

    fun fetchSmsOption(): String? {
        return prefs.getString(SMS_OPTION, null)
    }

    fun saveAccountNumber(accountNumber: String) {
        prefs.edit().apply {
            putString(ACCOUNT_NUMBER, accountNumber)
        }.apply()
    }

    fun saveSmsOption(sms_option: String) {
        prefs.edit().apply {
            putString(SMS_OPTION, sms_option)
        }.apply()
    }

    fun saveAccountStatus(accountStatus: String) {
        prefs.edit().apply {
            putString(ACCOUNT_STATUS, accountStatus)
        }.apply()
    }

    fun saveNotificationOption(notificationOption: Boolean) {
        prefs.edit().apply {
            putBoolean(NOTIFICATION_OPTION, notificationOption)
        }.apply()
    }

    fun saveFirstName(name: String) {
        prefs.edit().apply {
            putString(FIRST_NAME, name)
        }.apply()
    }

    fun fetchFirstName(): String? {
        return prefs.getString(FIRST_NAME, null)
    }

    fun fetchNotificationOption(): Boolean {
        return prefs.getBoolean(NOTIFICATION_OPTION, false)
    }

    fun saveLastName(name: String) {
        prefs.edit().apply {
            putString(LAST_NAME, name)
        }.apply()
    }

    fun fetchLastName(): String? {
        return prefs.getString(LAST_NAME, null)
    }

    fun fetchAccountStatus(): String? {
        return prefs.getString(ACCOUNT_STATUS, null)
    }

    fun fetchAccountNumber(): String? {
        return prefs.getString(ACCOUNT_NUMBER, null)
    }

    fun saveCode(code: String) {
        prefs.edit().apply {
            putString(SECURITY_CODE, code)
        }.apply()
    }

    fun fetchCode(): String? {
        return prefs.getString(SECURITY_CODE, null)
    }

    fun saveAccountType(accountType: String?) {
        prefs.edit().apply {
            putString(ACCOUNT_TYPE, accountType)
        }.apply()
    }

    fun saveSubAccountType(subAccountType: String?) {
        prefs.edit().apply {
            putString(SUB_ACCOUNT_TYPE, subAccountType)
        }.apply()
    }

    fun fetchSubAccountType(): String? {
        return prefs.getString(SUB_ACCOUNT_TYPE, null)
    }

    fun fetchAccountType(): String? {
        return prefs.getString(ACCOUNT_TYPE, null)
    }

    fun saveSecurityCodeObject(myObject: SecurityCodeResponseModel?) {
        prefs.edit().apply {
            val gson = Gson()
            val jsonString = gson.toJson(myObject)
            putString(SECURITY_CODE_OBJ, jsonString)
        }.apply()
    }

    fun clearAll() {
        val last_rating_time=fetchStringData(LAST_RATING_TIME)
        val LOCATION_PERMISSION=fetchBooleanData(LOCATION_PERMISSION)
        val email=fetchAccountEmailId()
        prefs.edit().clear().apply()
        HomeActivityMain.accountDetailsData = null
        HomeActivityMain.crossing = null
        HomeActivityMain.dateRangeModel = null
        HomeActivityMain.paymentHistoryListData = mutableListOf()
        saveStringData(LAST_RATING_TIME,last_rating_time)
        saveBooleanData(Companion.LOCATION_PERMISSION, LOCATION_PERMISSION)
    }

    fun setSessionTime(code: Long?) {
        prefs.edit().apply {
            putLong(SESSION_TIME, code ?: 0L)
        }.apply()
    }

    fun getSessionTime(): Long {
        return prefs.getLong(SESSION_TIME, 0L)
    }

//    fun saveSecurityCodeObject(myObject: GetSecurityCodeResponseModel) {
//        val editor = prefs.edit()
//        val gson = Gson()
//        val jsonString = gson.toJson(myObject)
//        editor.putString( SECURITY_CODE_OBJ, jsonString)
//        editor.commit()
//    }
//    fun fetchSecurityCodeObj(): GetSecurityCodeResponseModel? {
//        val gson = Gson()
//        val json: String? = prefs.getString(SECURITY_CODE_OBJ, "")
//        return gson.fromJson(json, GetSecurityCodeResponseModel::class.java)
//    }


    fun setAccountType(type: String?) {
        prefs.edit().apply {
            putString(ACCOUNT_TYPE, type)
        }.apply()
    }

    fun getAccountType(): String? {
        return prefs.getString(ACCOUNT_TYPE, null)
    }

    fun isSecondaryUser(isSecondaryUser: Boolean) {
        prefs.edit().apply {
            putBoolean(IS_SECONDARY, isSecondaryUser)
        }.apply()
    }

    fun getSecondaryUser(): Boolean {
        return prefs.getBoolean(IS_SECONDARY, false)
    }

    fun setLoggedInUser(loggedIn: Boolean) {
        prefs.edit().apply {
            putBoolean(LOGGED_IN_USER, loggedIn)
        }.apply()
    }

    fun getLoggedInUser(): Boolean {
        return prefs.getBoolean(LOGGED_IN_USER, false)
    }

    fun setNCId(type: String?) {
        prefs.edit().apply {
            putString(NC_ID, type)
        }.apply()
    }

    fun getNCId(): String? {
        return prefs.getString(NC_ID, null)
    }

    fun setFirebasePushToken(token: String) {
        prefs.edit().apply {
            putString(PUSH_TOKEN, token)
        }.apply()
    }

    fun getFirebaseToken(): String? {
        return prefs.getString(PUSH_TOKEN, null)
    }

    fun saveAccountEmailId(email: String?) {
        prefs.edit().apply {
            putString(USER_EMAIL_ID, email)
        }.apply()
    }

    fun fetchAccountEmailId(): String? {
        return prefs.getString(USER_EMAIL_ID, null)
    }

    fun saveUserName(email: String?) {
        prefs.edit().apply {
            putString(USER_NAME, email)
        }.apply()
    }

    fun fetchUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun saveUserCountryCode(countrycode: String?) {
        prefs.edit().apply {
            putString(USER_COUNTRYCODE, countrycode)
        }.apply()
    }

    fun fetchUserCountryCode(): String? {
        return prefs.getString(USER_COUNTRYCODE, null)
    }

    fun saveUserMobileNumber(mobilenumber: String?) {
        prefs.edit().apply {
            putString(USER_MOBILENUMBER, mobilenumber)
        }.apply()
    }

    fun fetchUserMobileNUmber(): String? {
        return prefs.getString(USER_MOBILENUMBER, null)
    }


    fun saveTouchIdEnabled(privateKey: Boolean) {
        prefs.edit().apply {
            putBoolean(TOUCH_ID_ENABLED, privateKey)
        }.apply()
    }
    fun fetchTouchIdEnabled(): Boolean {
        return prefs.getBoolean(TOUCH_ID_ENABLED, false)
    }

    fun saveZipCode(privateKey: String) {
        prefs.edit().apply {
            putString(ZIPCODE, privateKey)
        }.apply()
    }

    fun fetchZipCode(): Boolean {
        return prefs.getBoolean(ZIPCODE, false)
    }

    fun savePhoneNumber(privateKey: String) {
        prefs.edit().apply {
            putString(PHONE_NUMBER, privateKey)
        }.apply()
    }


    fun fetchPhoneNumber(): Boolean {
        return prefs.getBoolean(PHONE_NUMBER, false)
    }

    fun fetchSubCategoriesData(): ArrayList<CaseCategoriesModel> {
        val json = prefs.getString(SUB_CATEGORIES_DATA, null)
        val type = object : TypeToken<ArrayList<CaseCategoriesModel>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }

    fun saveSubCategoriesData(arrayList: List<CaseCategoriesModel?>?){
        val gson = Gson()
        val json = gson.toJson(arrayList)

        prefs.edit().apply {
            putString(SUB_CATEGORIES_DATA, json)
        }.apply()

    }


    fun saveBooleanData(key: String, privateKey: Boolean) {
        prefs.edit().apply {
            putBoolean(key, privateKey)
        }.apply()
    }

    fun fetchBooleanData(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun saveStringData(key: String, privateKey: String) {
        prefs.edit().apply {
            putString(key, privateKey)
        }.apply()
    }

    fun fetchStringData(key: String): String {
        return prefs.getString(key,"")?:""
    }
  fun saveIntData(key: String, privateKey:Int) {
        prefs.edit().apply {
            putInt(key, privateKey)
        }.apply()
    }

    fun fetchIntData(key: String): Int {
        return prefs.getInt(key,0)?:0
    }

    fun saveTwoFAEnabled(b: Boolean) {
        prefs.edit().apply {
            putBoolean(Constants.TWOFA_ENABLED, b)
        }.apply()
    }
    fun saveHasAskedForBiometric(b: Boolean) {
        prefs.edit().apply {
            putBoolean(Constants.HAS_ASKED_FOR_BIOMETRIC, b)
        }.apply()
    }

    fun getTwoFAEnabled() : Boolean {
        return prefs.getBoolean(Constants.TWOFA_ENABLED,false)

    }

    fun hasAskedForBiometric(): Boolean {
        return prefs.getBoolean(Constants.HAS_ASKED_FOR_BIOMETRIC,false)
    }

}