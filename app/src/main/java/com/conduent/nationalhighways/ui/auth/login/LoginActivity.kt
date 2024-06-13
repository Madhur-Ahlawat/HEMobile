package com.conduent.nationalhighways.ui.auth.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricPrompt
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.LRDSResponse
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.profile.ReplenishmentInformation
import com.conduent.nationalhighways.data.remote.ApiService
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
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity<FragmentLoginChangesBinding>(), View.OnClickListener {
    private var commaSeparatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var materialToolbar: MaterialToolbar? = null
    private lateinit var binding: FragmentLoginChangesBinding
    private lateinit var loginModel: LoginModel
    private var emailCheck: Boolean = false
    private var passwordCheck: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var from: String = ""
    private var crossingCount: Int = 0
    private var hasFaceBiometric = false
    private var hasTouchBiometric = false

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var sessionManager: SessionManager


    override fun observeViewModel() {
        lifecycleScope.launch {
            observe(viewModel.login, ::handleLoginResponse)
            observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
            observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)
            observe(dashboardViewModel.lrdsVal, ::handleLrdsResponse)

        }


    }

    private fun handleLrdsResponse(resource: Resource<LRDSResponse?>?) {

        when (resource) {

            is Resource.Success -> {

                if (resource.data?.srApprovalStatus?.uppercase().equals("APPROVED")) {
                    dismissLoaderDialog()
                    startNewActivityByClearingStack(LandingActivity::class.java) {
                        putString(Constants.SHOW_SCREEN, Constants.LRDS_SCREEN)
                    }
                } else {
                    crossingHistoryApi()
                    if (sessionManager.fetchUserName() != binding.edtEmail.getText().toString()
                            .trim()
                    ) {
                        sessionManager.saveTouchIdEnabled(false)
                        sessionManager.saveHasAskedForBiometric(false)
                    }

                    if (sessionManager.getTwoFAEnabled()) {
                        dismissLoaderDialog()
                        val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.TWOFA)
                        intent.putExtra(Constants.NAV_FLOW_FROM, from)
                        intent.putExtra(
                            Constants.CARD_VALIDATION_REQUIRED,
                            sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
                        )
                        intent.putExtra(Constants.FIRST_TYM_REDIRECTS, true)
                        startActivity(intent)
                    } else {
                        dashboardViewModel.getAccountDetailsData()
                    }
                    sessionManager.saveUserName(binding.edtEmail.getText().toString())
                }
            }

            is Resource.DataError -> {
                dismissLoaderDialog()
            }

            else -> {
                dismissLoaderDialog()
            }
        }
    }

    private fun displayBiometricDialog(title: String) {
        displayCustomMessage(title,
            getString(R.string.doyouwantenablebiometric),
            getString(R.string.enablenow_lower_case),
            getString(R.string.enablelater_lower_case),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    val intent = Intent(this@LoginActivity, BiometricActivity::class.java)
                    intent.putExtra(Constants.TWOFA, sessionManager.getTwoFAEnabled())
                    intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)

                    intent.putExtra(
                        Constants.FROM_LOGIN_TO_BIOMETRIC,
                        Constants.FROM_LOGIN_TO_BIOMETRIC_VALUE
                    )
                    intent.putExtra(
                        Constants.CARD_VALIDATION_REQUIRED,
                        sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
                    )
                    if (from == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
                        intent.putExtra(
                            Constants.NAV_FLOW_FROM,
                            Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS
                        )
                    } else {
                        intent.putExtra(Constants.NAV_FLOW_FROM, Constants.LOGIN)
                    }
                    startActivity(intent)
                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    if (sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)) {
                        redirectToAuthForRevalidate()
                    } else {
                        startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putString(Constants.NAV_FLOW_FROM, from)
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                        }
                    }
                }
            })
    }


    private fun handleAccountDetails(status: Resource<ProfileDetailModel?>?) {
        dismissLoaderDialog()

        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation

                if (status.data?.accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                    val intent = Intent(this@LoginActivity, AuthActivity::class.java)
                    intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                    intent.putExtra(Constants.CROSSINGCOUNT, crossingCount.toString())
                    intent.putExtra(Constants.PERSONALDATA, personalInformation)
                    intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
                    intent.putExtra(Constants.NAV_FLOW_FROM, from)
                    intent.putExtra(
                        Constants.CARD_VALIDATION_REQUIRED,
                        sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
                    )
                    intent.putExtra(
                        Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
                    )
                    startActivity(intent)
                } else {
                    if (!(sessionManager.hasAskedForBiometric() && sessionManager.fetchTouchIdEnabled())) {
                        sessionManager.saveHasAskedForBiometric(true)
                        if (hasTouchBiometric && hasFaceBiometric) {
                            displayBiometricDialog(getString(R.string.str_enable_face_ID_fingerprint))

                        } else if (hasFaceBiometric) {
                            displayBiometricDialog(getString(R.string.str_enable_face_ID))

                        } else {
                            displayBiometricDialog(getString(R.string.str_enable_touch_ID))

                        }
                    } else {

                        if (sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)) {
                            redirectToAuthForRevalidate()
                        } else {
                            startNewActivityByClearingStack(HomeActivityMain::class.java) {
                                putString(Constants.NAV_FLOW_FROM, from)
                                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            }
                        }

                    }

                }


            }

            is Resource.DataError -> {
                dismissLoaderDialog()
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

    private fun redirectToAuthForRevalidate() {
        val intent = Intent(this@LoginActivity, AuthActivity::class.java)
        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
        intent.putExtra(
            Constants.CARD_VALIDATION_REQUIRED,
            sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
        )
        intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
        intent.putExtra(Constants.NAV_FLOW_FROM, from)
        startActivity(intent)
    }

    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.transactionList != null) {
                        crossingCount = it.transactionList.count ?: 0
                    }
                }
            }

            is Resource.DataError -> {
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
        init()
        initCtrl()
    }

    override fun onResume() {
        super.onResume()
        if (displayFingerPrintPopup() && sessionManager.fetchBooleanData(SessionManager.LOGGED_OUT_FROM_DASHBOARD)) {
            fingerPrintLogin()
        }
    }


    private fun init() {
        sessionManager.saveBooleanData(SessionManager.SendAuthTokenStatus, false)
        BaseApplication.flowNameAnalytics = Constants.LOGIN
        BaseApplication.screenNameAnalytics = ""
        if (intent.hasExtra(Constants.NAV_FLOW_FROM)) {
            intent?.apply {
                from = getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
            }
        }


        materialToolbar = findViewById(R.id.tool_bar_lyt)
        materialToolbar?.visibility = View.GONE
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
                    ) || Utils.countOccurrenceOfChar(
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
                    || (Utils.countOccurrenceOfChar(
                        binding.edtEmail.editText.text.toString().trim(), '.'
                    ) < 1) || (Utils.countOccurrenceOfChar(
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
                                    Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars ?: ""
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
                    } else if (Utils.countOccurrenceOfChar(
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
        hasFaceBiometric = Utils.hasFaceId(this)
        hasTouchBiometric = Utils.hasTouchId(this)
        binding.apply {
            tvForgotPassword.setOnClickListener(this@LoginActivity)
            edtEmail.editText.addTextChangedListener { removeError() }
            binding.edtEmail.editText.setOnFocusChangeListener { _, b -> isEnable(b) }
            edtPwd.editText.doAfterTextChanged { passwordCheck() }
            btnLogin.setOnClickListener(this@LoginActivity)
            backButton.setOnClickListener(this@LoginActivity)
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

        when (status) {
            is Resource.Success -> {
                launchIntent(status)
            }

            is Resource.DataError -> {
                dismissLoaderDialog()
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
                dismissLoaderDialog()
                status?.errorModel?.message?.let { binding.edtEmail.setErrorText(it) }
                binding.btnLogin.isEnabled = true
            }
        }

    }

    private fun launchIntent(response: Resource.Success<LoginResponse?>) {
        if (sessionManager.fetchUserName() != binding.edtEmail.getText().toString()
                .trim()
        ) {

            sessionManager.saveTouchIdEnabled(false)
            sessionManager.saveHasAskedForBiometric(false)
        }
        sessionManager.saveUserName(binding.edtEmail.getText().toString())
        sessionManager.run {
            saveAuthToken(response.data?.accessToken ?: "")
            saveBooleanData(SessionManager.SendAuthTokenStatus, true)
            saveTwoFAEnabled(response.data?.require2FA == "true")
            saveRefreshToken(response.data?.refreshToken ?: "")
            setAccountType(response.data?.accountType ?: Constants.PERSONAL_ACCOUNT)
            isSecondaryUser(response.data?.isSecondary ?: false)
            saveAuthTokenTimeOut(response.data?.expiresIn ?: 0)
            saveAccountType(response.data?.accountType ?: "")
            saveBooleanData(
                SessionManager.CARD_VALIDATION_REQUIRED,
                response.data?.cardValidationRequired ?: false
            )
            setLoggedInUser(true)
        }

        if (sessionManager.getTwoFAEnabled()) {
            dismissLoaderDialog()
            val intent = Intent(this@LoginActivity, AuthActivity::class.java)
            intent.putExtra(Constants.NAV_FLOW_KEY, Constants.TWOFA)
            intent.putExtra(
                Constants.CARD_VALIDATION_REQUIRED,
                sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
            )
            intent.putExtra(Constants.NAV_FLOW_FROM, from)

            startActivity(intent)
        } else {
            dashboardViewModel.getLRDSResponse()
        }



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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                sessionManager.saveBooleanData(SessionManager.SendAuthTokenStatus, false)

                hideKeyboard()
                loginModel = LoginModel(
                    value = binding.edtEmail.getText().toString().trim(),
                    password = binding.edtPwd.getText().toString().trim(),
                    enable = true
                )

                showLoaderDialog()
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
        showLoaderDialog()
        getNewToken(api = api, sessionManager)
    }

    private fun hitAPIs(): () -> Unit? {
        dashboardViewModel.getAccountDetailsData()
        return {}
    }

    private fun getNewToken(api: ApiService, sessionManager: SessionManager) {
        sessionManager.fetchRefreshToken()?.let { refresh ->
            var responseOK: Boolean
            var response: Response<LoginResponse?>? = null

            BaseApplication.saveDateInSession(sessionManager)

            try {
                response = runBlocking {
                    api.refreshToken(refresh_token = refresh)
                }
                responseOK = response?.isSuccessful == true
            } catch (e: Exception) {
                responseOK = false
            }
            if (responseOK) {
                BaseApplication.saveToken(sessionManager, response)
                if (response?.body()?.mfaEnabled != null && response.body()?.mfaEnabled?.lowercase() == "true") {
                    sessionManager.saveTwoFAEnabled(true)
                } else {
                    sessionManager.saveTwoFAEnabled(false)
                }
                hitAPIs()
            }
        }
    }

}


