package com.heandroid.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("HE_MOBILE", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val Refresh_TOKEN = "user_token"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        Log.d("Session Manager::",token)
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to save auth token
     */
    fun saveRefrehToken(token: String) {
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
}