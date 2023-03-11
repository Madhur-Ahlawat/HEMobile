package com.conduent.nationalhighways.ui.auth.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.databinding.FragmentLoginChangesBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.biometric.BiometricActivity
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.KeystoreHelper
import com.conduent.nationalhighways.utils.Utility
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginChangesBinding>(), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var passwordVisibile: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentLoginChangesBinding = FragmentLoginChangesBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_log_in_dart_system))
    }

    override fun init() {
        binding.model = LoginModel(value = "", password = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        initBiometric()

        AdobeAnalytics.setScreenTrack(
            "login",
            "login",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login",
            sessionManager.getLoggedInUser()
        )


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initCtrl() {
        binding.apply {
            tvForgotUsername.setOnClickListener(this@LoginFragment)
            tvForgotPassword.setOnClickListener(this@LoginFragment)
            fingerprint.setOnClickListener(this@LoginFragment)
            edtEmail.doAfterTextChanged { checkButton() }
            edtPwd.doAfterTextChanged { checkButton() }
            btnLogin.setOnClickListener(this@LoginFragment)
        }
        if (displayFingerPrintPopup()) {
            binding.fingerprint.visible()
            fingerPrintLogin()
        } else {
            binding.fingerprint.gone()

        }

        binding.edtPwd.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtPwd.right - binding.edtPwd.compoundDrawables[right].bounds.width()) {

                    if (passwordVisibile) {
                        binding.edtPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtPwd.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisibile = false
                    } else {
                        binding.edtPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtPwd.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        passwordVisibile = true
                    }
                }
            }

            false
        }

        binding.fingerprint.setOnClickListener {
            if (!displayFingerPrintPopup()) {

                displayMessage(
                    getString(R.string.app_name),
                    getString(R.string.pleaseenablebiometric),
                    getString(R.string.str_ok),
                    "", null, null
                )
            } else {
                fingerPrintLogin()
            }

        }


    }

    override fun observer() {
        observe(viewModel.login, ::handleLoginResponse)
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
                showError(binding.root, status.errorMsg)


                AdobeAnalytics.setLoginActionTrackError(
                    "login",
                    "login",
                    "login",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
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

        if (sessionManager.fetchUserName() != binding.edtEmail.text.toString()) {
            displayBiometricDialog()
        } else {
            requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)

        }
        sessionManager.saveUserName(binding.edtEmail.text.toString())

        AdobeAnalytics.setLoginActionTrackError(
            "login",
            "login",
            "login",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "false",
            "manual",
            sessionManager.getLoggedInUser()
        )

    }

    private fun displayBiometricDialog() {
        displayCustomMessage(getString(R.string.enable_biometric),
            getString(R.string.doyouwantenablebiometric),
            getString(R.string.enablenow),
            getString(R.string.enablelater),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    requireActivity().openActivityWithData(BiometricActivity::class.java) {
                        putInt(
                            Constants.FROM_LOGIN_TO_BIOMETRIC,
                            Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE
                        )
                    }


                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)

                    // dialog.dismiss()
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {

                hideKeyboard()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.login(binding.model)


            }

            R.id.tv_forgot_username -> {
                AdobeAnalytics.setActionTrackError(
                    "forgot username",
                    "login",
                    "login",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    "success",
                    sessionManager.getLoggedInUser()
                )

                findNavController().navigate(R.id.action_loginFragment_to_forgotEmailFragment)
            }
            R.id.tv_forgot_password -> {
                AdobeAnalytics.setActionTrackError(
                    "forgot password",
                    "login",
                    "login",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    "success",
                    sessionManager.getLoggedInUser()
                )

                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
    }

    private fun checkButton() {
        if (Utils.isEmailValid(binding.edtEmail.text.toString()) && binding.edtPwd.length() > 5) {
            binding.model = LoginModel(
                value = binding.edtEmail.text.toString(),
                password = binding.edtPwd.text.toString(),
                enable = true
            )
        } else {
            binding.model = LoginModel(
                value = binding.edtEmail.text.toString(),
                password = binding.edtPwd.text.toString(),
                enable = false
            )
        }
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
                            requireActivity(), "Biometric is Disabled", Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            requireActivity(),
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
                        requireActivity(), "Biometric authentication failed", Toast.LENGTH_SHORT
                    ).show()
                }
            })

        val language = Locale.getDefault().displayLanguage
        if (language == "español") {
            promptInfo =
                BiometricPrompt.PromptInfo.Builder().setTitle("Inicio de sesión biométrico")
                    .setSubtitle("")
                    .setDescription("Inicie sesión con sus credenciales de biometría o seleccione Cancelar e introduzca la información de su cuenta y la contraseña.")
                    .setNegativeButtonText("CANCELAR").setConfirmationRequired(false).build()
        } else {
            promptInfo =
                BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Login").setSubtitle("")
                    .setDescription("Place your fingerprint on the sensor to login or select Cancel and enter your account information and password.")
                    .setNegativeButtonText("CANCEL").setConfirmationRequired(false).build()
        }
    }


    private fun onBiometricSuccessful() {
        val dateTime = System.currentTimeMillis().toString()

        val verificationToken = Utility.getSHA256HashedValue(
            "vendeor Id" + "|" + BuildConfig.VERSION_NAME + "|" + Build.MODEL + "|" + Build.VERSION.SDK_INT.toString() + "|" + sessionManager.fetchBiometricToken()
        )

        val keyHelper = KeystoreHelper.getInstance(requireActivity())
        val decryptedUserId = keyHelper?.decrypt(
            requireActivity(), "username"
        )

        val dataToBeSigned = "$decryptedUserId|firebase token|$verificationToken|$dateTime"


        val intent = Intent(requireActivity(), HomeActivityMain::class.java)
        startActivity(intent)

        // call login api
        /*  doLoginWithTouchID(
              decryptedUserId,
              SignatureHelper.getSignature(this, dataToBeSigned),
              dateTime
          )*/
    }

}


