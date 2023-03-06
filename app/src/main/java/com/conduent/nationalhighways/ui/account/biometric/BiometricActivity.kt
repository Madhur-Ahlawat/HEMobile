package com.conduent.nationalhighways.ui.account.biometric


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityBiometricBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.SignatureHelper
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BiometricActivity : BaseActivity<ActivityBiometricBinding>(), View.OnClickListener {

    lateinit var binding: ActivityBiometricBinding

    @Inject
    lateinit var sessionManager: SessionManager

    var mValue = Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun initViewBinding() {
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customToolbar(getString(R.string.biometrics))

        initCtrl()
    }

    private fun initCtrl() {

        binding.apply {
            toolBarLyt.backButton.setOnClickListener(this@BiometricActivity)
            btnSave.setOnClickListener(this@BiometricActivity)
            biometricCancel.setOnClickListener(this@BiometricActivity)
        }

        intent?.apply {
            mValue = getIntExtra(
                Constants.FROM_LOGIN_TO_BIOMETRIC,
                Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE
            )
        }

        if (mValue == Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE) {
            binding.toolBarLyt.backButton.gone()

        } else {
            binding.toolBarLyt.backButton.visible()
        }

        initBiomatric(this)

        binding.switchFingerprintLogin.isChecked = sessionManager.fetchTouchIdEnabled()

        binding.switchFingerprintLogin.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                val biometricManager = BiometricManager.from(this)
                when (biometricManager.canAuthenticate()) {
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        Toast.makeText(this, "No Hardware found", Toast.LENGTH_SHORT).show()
                    }
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Toast.makeText(this, "No Hardware unavailable", Toast.LENGTH_SHORT).show()

                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Toast.makeText(
                            this,
                            "Please enable the biometric from your device",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        displayFingerPrintPopup()
                    }
                }
            }
        }

    }

    private fun displayFingerPrintPopup() {
        Handler(Looper.getMainLooper()).post {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun observeViewModel() {
    }

    @SuppressLint("RestrictedApi")
    private fun initBiomatric(context: Context) {
        biometricPrompt = BiometricPrompt(this, ArchTaskExecutor.getMainThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == 7) // Too many attempts. try again later ( customised the toast message to below one)
                    {
                        Toast.makeText(context, "Biometric is Disabled", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Biometric authentication ${errString.toString().toLowerCase()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.switchFingerprintLogin.isChecked = false

                    //btnSave.disable()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    binding.switchFingerprintLogin.isChecked = true
                    // btnSave.enable()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    (mActivity as BaseActivity).displayMessage(
//                       "Biometric not successful. Log in using your account information and password.","Ok",
//                            BaseActivity.MessageType.APP_NAME,null
//                    )
                    // switchBiometric?.isChecked = false
                    // btnSave.disable()

                }
            })


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("")
            .setDescription("Place your fingerprint on the sensor to login or select Cancel and enter your account information and password.")
            .setNegativeButtonText("CANCEL")
            .setConfirmationRequired(false)
            .build()


    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.back_button -> {
               finish()
            }
            R.id.btn_save -> {
                saveBiometric()

            }
            R.id.biometric_cancel -> {
                if (mValue == Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE) {
                    navigateHomeActivity()
                    finish()
                } else {
                    finish()
                }            }
        }
    }

    private fun saveBiometric() {
        if (binding.switchFingerprintLogin.isChecked) {
            touchIdApiCall()
            sessionManager.saveTouchIdEnabled(true)

            Toast.makeText(this, "Biometric is Enabled", Toast.LENGTH_SHORT).show()

        } else {
            sessionManager.saveTouchIdEnabled(false)

            Toast.makeText(this, "Biometric is Disabled", Toast.LENGTH_SHORT).show()


        }
        if (mValue == Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE) {
            navigateHomeActivity()
            finish()
        } else {
            finish()
        }


    }

    private fun touchIdApiCall() {

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                //call API Here
                //SignatureHelper.getPublicKey(this@BiometricActivity)
                super.handleMessage(msg)
            }
        }
        Thread {
            SignatureHelper.generateKeyPair(this)
            val message = handler.obtainMessage()
            handler.sendMessage(message)
        }.start()
    }

    private fun navigateHomeActivity() {
        startActivity(
            Intent(this, HomeActivityMain::class.java)
        )
        finish()
    }
}