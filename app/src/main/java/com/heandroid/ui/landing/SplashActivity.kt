package com.heandroid.ui.landing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.utils.common.Constants

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            redirectHome()
        }, Constants.SPLASH_TIME_OUT)
    }

    private fun redirectHome() {
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()
    }

}