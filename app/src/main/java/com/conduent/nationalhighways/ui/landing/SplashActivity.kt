package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.BiometricUtils
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
            navigateNextScreen()
        }, Constants.SPLASH_TIME_OUT)
    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun navigateNextScreen() {
        return sessionManager.fetchAuthToken()?.let {
            if (Calendar.getInstance().timeInMillis - sessionManager.getSessionTime() < LogoutUtil.LOGOUT_TIME) {
                if (sessionManager.fetchTouchIdEnabled()){
                    showBiometrics()

                }else{
                    navigateHomeActivity()

                }
            } else {
                navigateLandingActivity()
            }
        } ?: run {
            navigateLandingActivity()
        }
    }

    private fun showBiometrics() {
        if (BiometricUtils.checkBioMetricAvailability(this)) {
            val prompt = BiometricUtils.biometricPromptForAllAuth(
                "Verify Credentials",
                "Confirm your identity before proceeding"
            )

            val biometricPrompt = BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        navigateLandingActivity()
//                        Toast.makeText(
//                            applicationContext,
//                            "Authentication error: $errString", Toast.LENGTH_SHORT
//                        ).show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(
                            applicationContext,
                            "Authentication succeeded!", Toast.LENGTH_SHORT
                        ).show()
                        navigateHomeActivity()
                    }

//                    override fun onAuthenticationFailed() {
//                        super.onAuthenticationFailed()
//                        Toast.makeText(
//                            applicationContext,
//                            "Authentication failed", Toast.LENGTH_SHORT
//                        ).show()
//                    }
                })
            biometricPrompt.authenticate(prompt)

        } else {
            navigateLandingActivity()
        }
    }

    private fun navigateHomeActivity() {
        startActivity(
            Intent(this, HomeActivityMain::class.java)
        )
        finish()
    }

    private fun navigateLandingActivity() {
        sessionManager.saveAuthToken(null)
        startActivity(
            Intent(this, LandingActivity::class.java)
        )
        finish()
    }


//    BiometricUtils.createPinBiometric(
//    this,
//    getString(R.string.verify_credentials),
//    getString(R.string.confirm_identity),
//    createPinCallBack
//    )
//
//    private val createPinCallBack = object : BiometricPrompt.AuthenticationCallback() {
//
//        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//            super.onAuthenticationError(errorCode, errString)
//            if (errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL ||
//                errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS
//            ) {
//
//            }
//
//        }
//
//        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//            super.onAuthenticationSucceeded(result)
//
//        }
//    }
}