package com.conduent.nationalhighways.ui.landing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivitySplashNewBinding
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashNewBinding? = null
    private var mIsRooted: Boolean =false


    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashNewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mIsRooted = checkIfRootConditionAndDisplayMessage()

        sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION,Utils.areNotificationsEnabled(this))

        if (!mIsRooted){
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




    }
    private fun checkIfRootConditionAndDisplayMessage(): Boolean {

        //In some devices app restarts if opened from launcher even if app is running and user is logged in,
        // Below code prevents app from restarting
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return true
        }

        return if (Utils.isRooted(this)) {
            if (!isFinishing) { // Added condition for a leaked window exception (if Activity is not finishing show dialog else not)
                displayAlertMessage(
                    getString(R.string.str_alert),
                    getString(R.string.str_rooted_device)
                )
            }
            true
        } else {
            false
        }
    }


    private fun redirectNextScreenWithHandler() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateLandingActivity()
        }, Constants.SPLASH_TIME_OUT)
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.e("TAG", "notification permission: "+it )
            sessionManager.saveBooleanData(SessionManager.NOTIFICATION_PERMISSION,it)
            redirectNextScreenWithHandler()
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
                navigateAuthActivity()
            } else {
                navigateLandingActivity()
            }
        } ?: run {
            navigateLandingActivity()
        }
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

    private fun displayAlertMessage(
        fTitle: String?,
        message: String

    ) {

        val dialog = Dialog(this)
        dialog.setCancelable(false)


        val binding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)



        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.title.text = fTitle
        binding.message.text = message
        binding.backLl.gone()
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