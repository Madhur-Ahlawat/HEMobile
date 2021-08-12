package com.heandroid

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.heandroid.utils.SessionManager

class DashboardPage : Activity() {
    lateinit var tokenString: String

    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)
        tokenString = findViewById<TextView>(R.id.token_id).toString()
        val token: String = SessionManager.USER_TOKEN
        tokenString = token
        Log.d("DashBoard Page ::token", token)
        Log.d("DashBoard Page ::", tokenString)
        sessionManager=SessionManager(this)
        sessionManager.fetchAuthToken()?.let {
            //requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d("DashBoard Page ::fetchAuthToken", it)
        }
    }
}