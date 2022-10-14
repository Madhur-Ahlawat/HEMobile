package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.logout.LogoutUtil

import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            redirectHome()
        }, Constants.SPLASH_TIME_OUT)
    }

    private fun redirectHome() {
        if (checkSession()) {
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun checkSession(): Boolean {
        return sessionManager.fetchAuthToken()?.let {
            if (Calendar.getInstance().timeInMillis - sessionManager.getSessionTime() < LogoutUtil.LOGOUT_TIME) {
                startActivity(
                    Intent(this, HomeActivityMain::class.java)
                )
                finish()
                false
            } else {
                sessionManager.clearAll()
                true
            }
        } ?: run {
            true
        }
    }

}