package com.conduent.nationalhighways.ui.auth.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.databinding.FragmentLoginChangesBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.biometric.BiometricActivity
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.KeystoreHelper
import com.conduent.nationalhighways.utils.Utility
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.*
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity<FragmentLoginChangesBinding>(), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var materialToolbar: MaterialToolbar? = null
    private lateinit var binding: FragmentLoginChangesBinding
    private lateinit var loginModel: LoginModel
    private var emailCheck: Boolean = false
    private var passwordCheck: Boolean = false


    @Inject
    lateinit var sessionManager: SessionManager


    override fun observeViewModel() {
        observe(viewModel.login, ::handleLoginResponse)

    }

    override fun initViewBinding() {
        binding = FragmentLoginChangesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initCtrl()
    }


    private fun init() {
        materialToolbar = findViewById(R.id.tool_bar_lyt)
        materialToolbar?.visibility = View.GONE
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        initBiometric()



        AdobeAnalytics.setScreenTrack(
            "login",
            "login",
            "english",
            "login", "",
            "login",
            sessionManager.getLoggedInUser()
        )


    }

    fun initCtrl() {

        binding.apply {
            tvForgotPassword.setOnClickListener(this@LoginActivity)
            edtEmail.editText.doAfterTextChanged { emailCheck() }
            edtPwd.editText.doAfterTextChanged { passwordCheck() }
            btnLogin.setOnClickListener(this@LoginActivity)
            backButton.setOnClickListener(this@LoginActivity)
        }

        if (displayFingerPrintPopup()) {
            fingerPrintLogin()
        }


    }


    private fun displayFingerPrintPopup(): Boolean {
        if (sessionManager.fetchTouchIdEnabled()) {

            return true
        }
        return false
    }

    private fun fingerPrintLogin() {
        Handler(Looper.getMainLooper()).post {
            biometricPrompt.authenticate(promptInfo)
        }
    }


    private fun handleLoginResponse(status: Resource<LoginResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {

                launchIntent(status)
            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == 5260) {
                    binding.edtEmail.setErrorText(getString(R.string.str_for_your_security_we_have_locked))
                } else {
                    binding.edtEmail.setErrorText(getString(R.string.str_incorrect_email_or_password))

                }


                AdobeAnalytics.setLoginActionTrackError(
                    "login",
                    "login",
                    "login",
                    "english",
                    "login",
                    "",
                    "true",
                    "manual",
                    sessionManager.getLoggedInUser()
                )
            }

            else -> {

            }
        }

    }

    private fun launchIntent(response: Resource.Success<LoginResponse?>) {

        sessionManager.run {
            saveAuthToken(response.data?.accessToken ?: "")
            saveRefreshToken(response.data?.refreshToken ?: "")
            setAccountType(response.data?.accountType ?: Constants.PERSONAL_ACCOUNT)
            isSecondaryUser(response.data?.isSecondary ?: false)
            saveAuthTokenTimeOut(response.data?.expiresIn ?: 0)
            saveAccountType(response.data?.accountType ?: "")
            setLoggedInUser(true)
        }

        if (sessionManager.fetchUserName() != binding.edtEmail.getText().toString().trim()) {
            displayBiometricDialog()
        } else {
            startNewActivityByClearingStack(HomeActivityMain::class.java)

        }
        sessionManager.saveUserName(binding.edtEmail.text.toString())

        AdobeAnalytics.setLoginActionTrackError(
            "login",
            "login",
            "login",
            "english",
            "login",
            "",
            "false",
            "manual",
            sessionManager.getLoggedInUser()
        )

    }

    private fun displayBiometricDialog() {
        displayCustomMessage(getString(R.string.str_enable_face_ID),
            getString(R.string.doyouwantenablebiometric),
            getString(R.string.enablenow),
            getString(R.string.enablelater),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    openActivityWithData(BiometricActivity::class.java) {
                        putInt(
                            Constants.FROM_LOGIN_TO_BIOMETRIC,
                            Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE
                        )
                    }

                    //dialog.dismiss()


                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {

                    startNewActivityByClearingStack(HomeActivityMain::class.java)

                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {

                hideKeyboard()
                loginModel = LoginModel(
                    value = binding.edtEmail.getText().toString().trim(),
                    password = binding.edtPwd.getText().toString().trim(),
                    enable = true
                )
                loader?.show(supportFragmentManager, Constants.LOADER_DIALOG)

                viewModel.login(loginModel)


            }

            R.id.back_button -> {
                startNormalActivityWithFinish(LandingActivity::class.java)
            }


            R.id.tv_forgot_password -> {
                AdobeAnalytics.setActionTrackError(
                    "forgot password",
                    "login",
                    "login",
                    "english",
                    "login",
                    "",
                    "success",
                    sessionManager.getLoggedInUser()
                )
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.FORGOT_PASSWORD_FLOW)
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                //findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment, bundle)
            }
        }
    }

    private fun emailCheck() {
        emailCheck =
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString()).matches()) {
                binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                false


            } else {
                binding.edtEmail.removeError()
                true


            }

        checkButton()


    }


    private fun passwordCheck() {
        passwordCheck = binding.edtPwd.getText().toString().trim().length >= 8

        checkButton()


    }

    private fun checkButton() {
        binding.btnLogin.isEnabled = emailCheck && passwordCheck
    }


    @SuppressLint("RestrictedApi")
    private fun initBiometric() {
        biometricPrompt = BiometricPrompt(this,
            ArchTaskExecutor.getMainThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    // Too many attempts. try again later ( customised the toast message to below one)
                    if (errorCode == 7) {
                        Toast.makeText(
                            this@LoginActivity, "Biometric is Disabled", Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Biometric authentication $errString",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    onBiometricSuccessful()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@LoginActivity, "Biometric authentication failed", Toast.LENGTH_SHORT
                    ).show()
                }
            })


        promptInfo =
            BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Login").setSubtitle("")
                .setDescription("Place your fingerprint on the sensor to login or select Cancel and enter your account information and password.")
                .setNegativeButtonText("CANCEL").setConfirmationRequired(false).build()

    }


    private fun onBiometricSuccessful() {
        val dateTime = System.currentTimeMillis().toString()

        val verificationToken = Utility.getSHA256HashedValue(
            "vendeor Id" + "|" + BuildConfig.VERSION_NAME + "|" + Build.MODEL + "|" + Build.VERSION.SDK_INT.toString() + "|" + sessionManager.fetchBiometricToken()
        )

        val keyHelper = KeystoreHelper.getInstance(this)
        val decryptedUserId = keyHelper?.decrypt(
            this, "username"
        )

        val dataToBeSigned = "$decryptedUserId|firebase token|$verificationToken|$dateTime"


        val intent = Intent(this, HomeActivityMain::class.java)
        startActivity(intent)

        // call login api
        /*  doLoginWithTouchID(
              decryptedUserId,
              SignatureHelper.getSignature(this, dataToBeSigned),
              dateTime
          )*/
    }

}


