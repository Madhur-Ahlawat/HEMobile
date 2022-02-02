package com.heandroid.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.heandroid.model.GetSecurityCodeResponseModel
import com.google.gson.Gson

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
       private var prefs: SharedPreferences = context.getSharedPreferences("HE_MOBILE", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val Refresh_TOKEN = "refresh_token"
        const val ACCOUNT_NUMBER="account_number"
        const val SECURITY_CODE="security_code"
        const val SECURITY_CODE_OBJ="security_code_obj"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        Log.d("Session Manager::",token)
        // todo use scope variablde
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

    fun saveSecurityCodeObject(myObject: GetSecurityCodeResponseModel) {
        val editor = prefs.edit()
        val gson = Gson()
        val jsonString = gson.toJson(myObject)
        editor.putString( SECURITY_CODE_OBJ, jsonString)
        editor.commit()
    }
    fun fetchSecurityCodeObj(): GetSecurityCodeResponseModel? {
        val gson = Gson()
        val json: String? = prefs.getString(SECURITY_CODE_OBJ, "")
        return gson.fromJson(json, GetSecurityCodeResponseModel::class.java)
    }



}