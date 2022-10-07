package com.heandroid.utils.common

import android.content.SharedPreferences
import com.google.gson.Gson
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
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
        const val ACCOUNT_TYPE = "account_type"
        const val SUB_ACCOUNT_TYPE = "sub_account_type"
        const val SECURITY_CODE = "security_code"
        const val SESSION_TIME = "session_time"
        const val SECURITY_CODE_OBJ = "security_code_obj"
        const val IS_USER_LOGIN = "is_user_login"
        const val IS_SECONDARY = "is_secondary_user"
        const val LOGGED_IN_USER = "logged_in_user"
        const val NC_ID = "nc_id"
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

    fun saveAccountNumber(accountNumber: String) {
        prefs.edit().apply {
            putString(ACCOUNT_NUMBER, accountNumber)
        }.apply()
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

    fun saveAccountType(accountType: String) {
        prefs.edit().apply {
            putString(ACCOUNT_TYPE, accountType)
        }.apply()
    }

    fun saveSubAccountType(subAccountType: String) {
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
        prefs.edit().clear().apply()
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

}