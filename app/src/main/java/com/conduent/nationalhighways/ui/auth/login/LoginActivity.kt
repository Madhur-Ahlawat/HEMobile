package com.conduent.nationalhighways.ui.auth.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricPrompt
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.LRDSResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.databinding.FragmentLoginChangesBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.biometric.BiometricActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.SessionManager.Companion.LAST_LOGIN_EMAIL
import com.conduent.nationalhighways.utils.extn.*
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity<FragmentLoginChangesBinding>(), View.OnClickListener {
    private var commaSeparatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private val viewModel: LoginViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var materialToolbar: MaterialToolbar? = null
    private lateinit var binding: FragmentLoginChangesBinding
    private lateinit var loginModel: LoginModel
    private var emailCheck: Boolean = false
    private var passwordCheck: Boolean = false
    private var twoFAEnable: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var from: String = ""
    private var crossingCount: Int = 0
    private var hasFaceBiometric=false
    private var hasTouchBiometric=false

    @Inject
    lateinit var sessionManager: SessionManager


    override fun observeViewModel() {
        lifecycleScope.launch {
            observe(viewModel.login, ::handleLoginResponse)
            observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
            observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)
            observe(dashboardViewModel.lrdsVal, ::handleLrdsResposne)

        }


    }

    private fun handleLrdsResposne(resource: Resource<LRDSResponse?>?) {
        Log.e("TAG", "handleLrdsResposne: ")
        when (resource) {

            is Resource.Success -> {
                Log.e("TAG", "handleLrdsResposne: statusCode " + resource.data?.srStatus)
                if (resource.data?.statusCode == null) {
                    startNewActivityByClearingStack(LandingActivity::class.java) {
                        putString(Constants.SHOW_SCREEN, Constants.LRDS_SCREEN)
                    }
                } else {
                    crossingHistoryApi()


                    if (sessionManager.fetchStringData(LAST_LOGIN_EMAIL) != binding.edtEmail.getText().toString()
                            .trim()
                    ) {
                        if (loader?.isVisible == true) {
                            loader?.dismiss()
                        }
                        displayBiometricDialog(getString(R.string.str_enable_face_ID))


                    } else {
                        if (twoFAEnable) {
                            if (loader?.isVisible == true) {
                                loader?.dismiss()
                            }
                            val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                            intent.putExtra(Constants.NAV_FLOW_KEY, Constants.TWOFA)
                            startActivity(intent)
                        } else {
                            dashboardViewModel.getAccountDetailsData()

                        }
                    }

                    sessionManager.saveUserName(binding.edtEmail.text.toString())


                }
            }

            else -> {

            }
        }
    }


    private fun handleAccountDetails(status: Resource<AccountResponse?>?) {


        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation


                if (status.data?.accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                    if (loader?.isVisible == true) {
                        loader?.dismiss()
                    }

//                    if (crossingCount >= 0) {
                        val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                        intent.putExtra(Constants.CROSSINGCOUNT, crossingCount.toString())
                        intent.putExtra(Constants.PERSONALDATA, personalInformation)
                        intent.putExtra(Constants.NAV_FLOW_FROM, from)
                        intent.putExtra(
                            Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
                        )
                        startActivity(intent)
//                    }
                } else {
                    startNewActivityByClearingStack(HomeActivityMain::class.java) {
                        putString(Constants.NAV_FLOW_FROM, from)
                        putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    }

                }


            }

            is Resource.DataError -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
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

    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.transactionList != null) {
                        crossingCount = it.transactionList.count ?: 0
                    }
                }
            }

            is Resource.DataError -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
            }

            else -> {
            }
        }
    }


    private fun crossingHistoryApi() {
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 0,
            transactionType = Constants.TOLL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "",
            endDate = DateUtils.currentDate() ?: ""
        )
        dashboardViewModel.crossingHistoryApiCall(request)
    }

    override fun initViewBinding() {
        binding = FragmentLoginChangesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hasFaceBiometric = Utils.hasFaceId(this)

        hasTouchBiometric = Utils.hasTouchId(this)



        init()
        initCtrl()
    }


    private fun init() {
        BaseApplication.flowNameAnalytics=Constants.LOGIN
        BaseApplication.screenNameAnalytics=""
        if (intent.hasExtra(Constants.NAV_FLOW_FROM)) {
            intent?.apply {
                from = getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
            }
        }


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


    private fun isEnable(b: Boolean) {
        if (!b) {
            emailCheck = if (binding.edtEmail.editText.text.toString().trim().isNotEmpty()) {
                if (!Utils.isLastCharOfStringACharacter(
                        binding.edtEmail.editText.text.toString().trim()
                    ) || Utils.countOccurenceOfChar(
                        binding.edtEmail.editText.text.toString().trim(), '@'
                    ) > 1 || binding.edtEmail.editText.text.toString().trim().contains(
                        Utils.TWO_OR_MORE_DOTS
                    ) || (binding.edtEmail.editText.text.toString().trim().last()
                        .toString() == "." || binding.edtEmail.editText.text
                        .toString().first().toString() == ".")
                    || (binding.edtEmail.editText.text.toString().trim().last()
                        .toString() == "-" || binding.edtEmail.editText.text.toString()
                        .first()
                        .toString() == "-")
                    || (Utils.countOccurenceOfChar(
                        binding.edtEmail.editText.text.toString().trim(), '.'
                    ) < 1) || (Utils.countOccurenceOfChar(
                        binding.edtEmail.editText.text.toString().trim(), '@'
                    ) < 1)
                ) {
                    binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                    false
                } else {
                    if (Utils.hasSpecialCharacters(
                            binding.edtEmail.editText.text.toString().trim(),
                            Utils.splCharEmailCode
                        )
                    ) {
                        filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                            Utils.LOWER_CASE,
                            binding.edtEmail.getText().toString().trim()
                        )
                        filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                            Utils.UPPER_CASE,
                            binding.edtEmail.getText().toString().trim()
                        )
                        filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                            Utils.DIGITS,
                            binding.edtEmail.getText().toString().trim()
                        )
                        filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                            Utils.ALLOWED_CHARS_EMAIL,
                            binding.edtEmail.getText().toString().trim()
                        )
                        commaSeparatedString =
                            Utils.makeCommaSeperatedStringForPassword(
                                Utils.removeAllCharacters(
                                    Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
                                )
                            )
                       if (!Patterns.EMAIL_ADDRESS.matcher(
                                binding.edtEmail.getText().toString()
                            ).matches()
                        ) {
                            binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                            false
                        } else {
                            binding.edtEmail.removeError()
                            true
                        }
                    } else if (Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.text.toString().trim(), '@'
                        ) !in (1..1)
                    ) {
                        binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                        false
                    } else {
                        binding.edtEmail.removeError()
                        true
                    }
                }

            } else {
                binding.edtEmail.removeError()
                false
            }
        }

        checkButton()
    }

    fun initCtrl() {

        binding.apply {
            tvForgotPassword.setOnClickListener(this@LoginActivity)
            edtEmail.editText.addTextChangedListener { removeError() }
            binding.edtEmail.editText.setOnFocusChangeListener { _, b -> isEnable(b) }

            edtPwd.editText.doAfterTextChanged { passwordCheck() }
            btnLogin.setOnClickListener(this@LoginActivity)
            backButton.setOnClickListener(this@LoginActivity)

        }

        if (displayFingerPrintPopup()) {
            fingerPrintLogin()
        }


    }

    private fun removeError() {
        binding.edtEmail.removeError()

    }


    private fun displayFingerPrintPopup(): Boolean {
        return sessionManager.fetchTouchIdEnabled()
    }

    private fun fingerPrintLogin() {
        Handler(Looper.getMainLooper()).post {
            biometricPrompt.authenticate(promptInfo)
        }
    }


    private fun handleLoginResponse(status: Resource<LoginResponse?>?) {
        /* if (loader?.isVisible == true) {
             loader?.dismiss()
         }*/
        when (status) {
            is Resource.Success -> {
                launchIntent(status)
            }

            is Resource.DataError -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
                if (status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    binding.btnLogin.isEnabled = true

                    if (status.errorModel?.errorCode == 5260) {
                        binding.edtEmail.setErrorText(getString(R.string.str_for_your_security_we_have_locked))
                    } else if (status.errorModel?.error.equals("unauthorized", true)) {
                        binding.edtEmail.setErrorText(getString(R.string.str_incorrect_email_or_password))

                    } else {
                        status.errorModel?.message?.let { binding.edtEmail.setErrorText(it) }

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

            }

            else -> {
                status?.errorModel?.message?.let { binding.edtEmail.setErrorText(it) }

                binding.btnLogin.isEnabled = true
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
//            saveUserName(binding.edtEmail.getText().toString())
            twoFAEnable = response.data?.require2FA == "true"
        }

        if (sessionManager.fetchUserName() != binding.edtEmail.getText().toString()
                .trim()
        ) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }

            if (hasTouchBiometric&&hasFaceBiometric){
                displayBiometricDialog(getString(R.string.str_enable_face_ID_fingerprint))

            }else if (hasFaceBiometric){
                displayBiometricDialog(getString(R.string.str_enable_face_ID))

            }else{
                displayBiometricDialog(getString(R.string.str_enable_touch_ID))

            }


        } else {
            if (twoFAEnable) {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }
                val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                intent.putExtra(Constants.NAV_FLOW_KEY, Constants.TWOFA)
                startActivity(intent)
            } else {
                dashboardViewModel.getLRDSResponse()
            }
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

    private fun displayBiometricDialog(title: String) {
        displayCustomMessage(title,
            getString(R.string.doyouwantenablebiometric),
            getString(R.string.enablenow),
            getString(R.string.enablelater),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    val intent = Intent(this@LoginActivity, BiometricActivity::class.java)
                    intent.putExtra(Constants.TWOFA, twoFAEnable)
                    intent.putExtra(
                        Constants.FROM_LOGIN_TO_BIOMETRIC,
                        Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE
                    )
                    intent.putExtra(Constants.NAV_FLOW_FROM, from)

                    startActivity(intent)


                    //dialog.dismiss()


                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    if (twoFAEnable) {
                        val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.TWOFA)
                        intent.putExtra(Constants.NAV_FLOW_FROM, from)
                        startActivity(intent)
                    } else {
                        dashboardViewModel.getAccountDetailsData()

                    }
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
                NewCreateAccountRequestModel.emailAddress = ""
                val intent = Intent(this, AuthActivity::class.java)
                intent.putExtra(Constants.NAV_FLOW_KEY, Constants.FORGOT_PASSWORD_FLOW)

                startActivity(intent)
            }
        }
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
        val intent = Intent(this, HomeActivityMain::class.java)
        intent.putExtra(Constants.FIRST_TYM_REDIRECTS, true)
        startActivity(intent)
    }


}


