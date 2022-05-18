package com.heandroid.utils.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session manager to save and fetch data from SharedPreferences
 */

@Singleton
class SessionManager @Inject constructor (@ApplicationContext context: Context) {
       private var prefs: SharedPreferences = context.getSharedPreferences("HE_MOBILE", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val Refresh_TOKEN = "refresh_token"
        const val ACCOUNT_NUMBER="account_number"
        const val ACCOUNT_TYPE="account_type"
        const val SECURITY_CODE="security_code"
        const val SESSION_TIME="session_time"
        const val SECURITY_CODE_OBJ="security_code_obj"
        const val IS_USER_LOGIN="is_user_login"
        const val IS_SECONDARY="is_secondary_user"
        const val LOGGED_IN_USER="logged_in_user"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String?) {
        Log.d("Session Manager::",token?:"")
        // todo use scope variable
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to save auth token
     */
    fun saveRefreshToken(token: String) {
        Log.d("Session Manager::",token)
        val editor = prefs.edit()
        editor.putString(Refresh_TOKEN, token)
        editor.apply()
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
        Log.d("Session Manager::",accountNumber)
        val editor = prefs.edit()
        editor.putString(ACCOUNT_NUMBER, accountNumber)
        editor.apply()
    }
    fun fetchAccountNumber(): String? {
        return prefs.getString(ACCOUNT_NUMBER, null)
    }

    fun saveCode(code: String) {
        Log.d("Session Manager::",code)
        val editor = prefs.edit()
        editor.putString(SECURITY_CODE, code)
        editor.apply()
    }
    fun fetchCode(): String? {
        return prefs.getString(SECURITY_CODE, null)
    }

    fun saveAccountType(accountType: String) {
        Log.d("Session Manager::",accountType)
        val editor = prefs.edit()
        editor.putString(ACCOUNT_TYPE, accountType)
        editor.apply()
    }
    fun fetchAccountType(): String? {
        return prefs.getString(ACCOUNT_TYPE, null)
    }

    fun saveSecurityCodeObject(myObject: SecurityCodeResponseModel?) {
        val editor = prefs.edit()
        val gson = Gson()
        val jsonString = gson.toJson(myObject)
        editor.putString( SECURITY_CODE_OBJ, jsonString)
        editor.commit()
    }

    fun clearAll() {
        prefs.edit().clear().apply()

    }

    fun setSessionTime(code: Long?) {
        val editor = prefs.edit()
        editor.putLong(SESSION_TIME, code?:0L)
        editor.apply()
    }

    fun getSessionTime() : Long{
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
        val editor = prefs.edit()
        editor.putString(ACCOUNT_TYPE,type)
        editor.apply()
    }

    fun getAccountType() : String?{
        return prefs.getString(ACCOUNT_TYPE ,null)
    }

    fun isSecondaryUser(isSecondaryUser: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_SECONDARY,isSecondaryUser)
        editor.apply()
    }

    fun getSecondaryUser() : Boolean{
        return prefs.getBoolean(IS_SECONDARY ,false)
    }

    fun setLoggedInUser(loggedIn: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(LOGGED_IN_USER,loggedIn)
        editor.apply()
    }

    fun getLoggedInUser() : Boolean{
        return prefs.getBoolean(LOGGED_IN_USER ,false)
    }

}