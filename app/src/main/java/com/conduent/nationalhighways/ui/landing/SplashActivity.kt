package com.conduent.nationalhighways.ui.landing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivitySplashNewBinding
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.BiometricUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashNewBinding? = null

    @Inject
    lateinit var sessionManager: SessionManager
    var firstTym = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashNewBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        if (!Utils.areNotificationsEnabled(this)) {


            // Notifications are not enabled, request the user to enable them
            if (Build.VERSION.SDK_INT >= 33) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                displayCustomMessage(
                    resources.getString(R.string.str_notification_title),
                    resources.getString(R.string.str_notification_desc),
                    resources.getString(R.string.str_allow),
                    resources.getString(R.string.str_dont_allow)
                )

            }
        } else {
            redirectNextScreenWithHandler()

        }


    }


    private fun redirectNextScreenWithHandler() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateNextScreen()
        }, Constants.SPLASH_TIME_OUT)
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            redirectNextScreenWithHandler()
        }


    override fun onResume() {
        super.onResume()
        if (!firstTym) {
            redirectNextScreenWithHandler()
        }
        if (firstTym) {
            firstTym = false
        }
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun navigateNextScreen() {
        return sessionManager.fetchAuthToken()?.let {
            if (Calendar.getInstance().timeInMillis - sessionManager.getSessionTime() < LogoutUtil.LOGOUT_TIME) {
                navigateAuthActivity()
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
        val intent = Intent(this, HomeActivityMain::class.java)
        intent.putExtra(Constants.FIRST_TYM_REDIRECTS, true)
        startActivity(intent)
        finish()
    }

    private fun navigateAuthActivity() {
        startActivity(
            Intent(this, LoginActivity::class.java)
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

    private fun displayCustomMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
    ) {

        val dialog = Dialog(this)
        dialog.setCancelable(false)


        val binding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)



        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.title.text = fTitle
        binding.message.text = message
        binding.cancelBtn.text = negativeBtnTxt
        binding.okBtn.text = positiveBtnTxt
        binding.cancelBtn.setOnClickListener {
            redirectNextScreenWithHandler()
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            Utils.redirectToNotificationPermissionSettings(this)
            dialog.dismiss()
        }
        dialog.show()


    }

}