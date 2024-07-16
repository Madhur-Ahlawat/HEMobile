package com.conduent.nationalhighways.ui.account.biometric


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ReplenishmentInformation
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityBiometricBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.toolbar
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BiometricActivity : BaseActivity<ActivityBiometricBinding>(), View.OnClickListener,
    LogoutListener {

    private var biometricToggleButtonState: String? = null
    lateinit var binding: ActivityBiometricBinding
    private var twoFA: Boolean = false
    private var suspended: Boolean = false
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val toggleDelay: Long = 200

    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private var isScreenLaunchedBefore: Boolean = false
    private var isAuthenticated: Boolean = false
    private var cardValidationRequired: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    var navFlowFrom: String = ""
    var navFlowCall: String = ""
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var crossingCount: String = ""
    private var currentBalance: String = ""

    @Inject
    lateinit var api: ApiService

    override fun initViewBinding() {
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.biometrics))

        initCtrl()
    }

    private fun initCtrl() {
        if (intent.hasExtra(Constants.NAV_FLOW_FROM)) {
            navFlowFrom = intent.getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
        }
        if (intent.hasExtra(Constants.PAYMENT_LIST_DATA) && intent.getParcelableArrayListExtra<CardListResponseModel>(
                Constants.PAYMENT_LIST_DATA
            ) != null
        ) {
            paymentList =
                intent.getParcelableArrayListExtra(Constants.PAYMENT_LIST_DATA)
        }

        if (intent.getParcelableExtra<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                intent.getParcelableExtra(Constants.ACCOUNTINFORMATION)
        }

        if (intent.hasExtra(Constants.CARD_VALIDATION_REQUIRED)) {
            cardValidationRequired =
                intent.getBooleanExtra(Constants.CARD_VALIDATION_REQUIRED, false)
        }
        if (intent.hasExtra(Constants.NAV_FLOW_KEY)) {
            navFlowCall = intent.getStringExtra(Constants.NAV_FLOW_KEY) ?: ""
        }
        if (intent.hasExtra(Constants.CROSSINGCOUNT)) {
            crossingCount = intent.getStringExtra(Constants.CROSSINGCOUNT) ?: ""
        }
        if (intent.hasExtra(Constants.CURRENTBALANCE)) {
            currentBalance = intent.getStringExtra(Constants.CURRENTBALANCE) ?: ""
        }

        if (intent.getParcelableExtra<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                intent.getParcelableExtra(Constants.PERSONALDATA)

        }
        if (intent.getParcelableExtra<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                intent.getParcelableExtra(Constants.ACCOUNTINFORMATION)
        }

        if (navFlowFrom == Constants.TWOFA || navFlowFrom == Constants.LOGIN || navFlowFrom == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            binding.toolBarLyt.titleTxt.text = getString(R.string.biometrics)
            binding.toolBarLyt.backButton.gone()
            binding.biometricCancel.visible()
        } else {
            binding.toolBarLyt.titleTxt.text = getString(R.string.str_profile_biometrics)
            binding.toolBarLyt.backButton.visible()
            binding.biometricCancel.gone()
        }

        twoFA = intent.getBooleanExtra(Constants.TWOFA, false)
        suspended = accountInformation?.status.equals(Constants.SUSPENDED, true)

        Log.e("TAG", "initCtrl: suspended " + suspended)
        binding.apply {
            toolBarLyt.backButton.setOnClickListener(this@BiometricActivity)
            btnSave.setOnClickListener(this@BiometricActivity)
            biometricCancel.setOnClickListener(this@BiometricActivity)
            biometricToggleButtonState = if (switchFingerprintLogin.isChecked) "on" else "off"
        }

        initBiometric(this)

        binding.switchFingerprintLogin.isChecked = sessionManager.fetchTouchIdEnabled()
        if (sessionManager.fetchTouchIdEnabled()) {
            binding.biometricCancel.gone()
        }
        binding.switchFingerprintLogin.setOnCheckedChangeListener { _, isChecked ->
            biometricToggleButtonState =
                if (binding.switchFingerprintLogin.isChecked) "on" else "off"
            binding.switchFingerprintLogin.contentDescription = if (isChecked) {
                biometricToggleButtonState + " Toggle to ${if (biometricToggleButtonState.equals("yes")) "turn off" else "turn on"}"
            } else {
                biometricToggleButtonState + " Toggle to ${if (biometricToggleButtonState.equals("yes")) "turn on" else "turn off"}"
            }
            if (isChecked) {
                if (!sessionManager.fetchTouchIdEnabled()) {
                    val biometricManager = BiometricManager.from(this)
                    when (biometricManager.canAuthenticate()) {
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                            Toast.makeText(this, "No Hardware found", Toast.LENGTH_SHORT).show()
                            binding.switchFingerprintLogin.postDelayed({
                                binding.switchFingerprintLogin.isChecked = false
                            }, toggleDelay)
                        }

                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            Toast.makeText(this, "No Hardware unavailable", Toast.LENGTH_SHORT)
                                .show()
                            binding.switchFingerprintLogin.postDelayed({
                                binding.switchFingerprintLogin.isChecked = false
                            }, toggleDelay)
                        }

                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                            displayAccountSettingsDialog()
                        }

                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            displayFingerPrintPopup()
                        }

                        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                            Toast.makeText(
                                this,
                                "Biometric security update required!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            binding.switchFingerprintLogin.postDelayed({
                                binding.switchFingerprintLogin.isChecked = false
                            }, toggleDelay)
                        }

                        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                            Toast.makeText(this, "Biometric unsupported!", Toast.LENGTH_SHORT)
                                .show()
                            binding.switchFingerprintLogin.postDelayed({
                                binding.switchFingerprintLogin.isChecked = false
                            }, toggleDelay)
                        }

                        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                            Toast.makeText(this, "Biometric status unknown!", Toast.LENGTH_SHORT)
                                .show()
                            binding.switchFingerprintLogin.postDelayed({
                                binding.switchFingerprintLogin.isChecked = false
                            }, toggleDelay)
                        }
                    }

                } else {
                    binding.btnSave.isEnabled = false
                }
            } else {
                binding.btnSave.isEnabled = sessionManager.fetchTouchIdEnabled()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (binding.switchFingerprintLogin.isChecked && !isScreenLaunchedBefore) {
            isScreenLaunchedBefore = true
            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this, "No Hardware found", Toast.LENGTH_SHORT).show()
                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(this, "No Hardware unavailable", Toast.LENGTH_SHORT).show()
                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)

                }

                BiometricManager.BIOMETRIC_SUCCESS -> {

                }

                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    Toast.makeText(this, "Biometric security update required!", Toast.LENGTH_SHORT)
                        .show()

                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)
                }

                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    Toast.makeText(this, "Biometric unsupported!", Toast.LENGTH_SHORT).show()
                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)
                }

                BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                    Toast.makeText(this, "Biometric status unknown!", Toast.LENGTH_SHORT).show()
                    binding.switchFingerprintLogin.postDelayed({
                        binding.switchFingerprintLogin.isChecked = false
                    }, toggleDelay)
                }
            }
        }
    }

    private fun displayAccountSettingsDialog() {
        displayCustomMessage(getString(R.string.enable_biometric),
            getString(R.string.biometric_has_not_been_setup),
            getString(R.string.goto_settings),
            getString(R.string.cancel),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    binding.switchFingerprintLogin.isChecked = false

                    Utils.gotoMobileSetting(this@BiometricActivity)
                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    binding.switchFingerprintLogin.isChecked = false
                    dialog.dismiss()
                }
            })
    }


    private fun displayFingerPrintPopup() {
        Handler(Looper.getMainLooper()).post {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun observeViewModel() {
        observe(dashboardViewModel.crossingHistoryVal, ::crossingHistoryResponse)
    }


    @SuppressLint("RestrictedApi")
    private fun initBiometric(context: Context) {
        biometricPrompt = BiometricPrompt(this, ArchTaskExecutor.getMainThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    binding.btnSave.isEnabled = false

                    if (errorCode == 7) // Too many attempts. try again later ( customised the toast message to below one)
                    {
                        Toast.makeText(context, "Biometric is Disabled", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Biometric authentication ${errString.toString().lowercase()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.switchFingerprintLogin.isChecked = false
                    isAuthenticated = false
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    binding.btnSave.isEnabled = true
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
                if (binding.switchFingerprintLogin.isChecked && isAuthenticated) {
                    sessionManager.saveTouchIdEnabled(true)
                    goToHomeActivity()
                } else if (!binding.switchFingerprintLogin.isChecked) {
                    sessionManager.saveTouchIdEnabled(false)
                    isAuthenticated = false
                    goToHomeActivity()
                }

            }

            R.id.biometric_cancel -> {
                goToHomeActivity()
            }
        }
    }

    private fun redirectToAuthForRevalidate(from: String) {
        val intent = Intent(this@BiometricActivity, AuthActivity::class.java)
        intent.putExtra(
            Constants.CARD_VALIDATION_REQUIRED,
            sessionManager.fetchBooleanData(SessionManager.CARD_VALIDATION_REQUIRED)
        )
        intent.putExtra(Constants.NAV_FLOW_KEY, from)
        intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)
        intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
        intent.putParcelableArrayListExtra(Constants.PAYMENT_LIST_DATA, paymentList as ArrayList)
        startActivity(intent)
    }

    private fun goToHomeActivity() {
        if (accountInformation?.inactiveStatus == true) {
            redirectToAuthForRevalidate(Constants.IN_ACTIVE)
        } else if (cardValidationRequired) {
            redirectToAuthForRevalidate(Constants.CARD_VALIDATION_REQUIRED)
        } else if (suspended) {
            showLoaderDialog()
            crossingHistoryApi()
        } else {
            startNewActivityByClearingStack(HomeActivityMain::class.java) {
                if (navFlowFrom == (Constants.TWOFA) || navFlowFrom == (Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) || navFlowCall == (Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS)) {

                    if (navFlowCall == (Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS)) {
                        putString(
                            Constants.NAV_FLOW_FROM,
                            Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS
                        )
                    } else {
                        putString(
                            Constants.NAV_FLOW_FROM,
                            navFlowFrom
                        )
                    }
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                } else if (navFlowFrom == Constants.LOGIN) {
                    putString(
                        Constants.NAV_FLOW_FROM,
                        Constants.BIOMETRIC_CHANGE
                    )
                    putBoolean(
                        Constants.GO_TO_SUCCESS_PAGE,
                        false
                    )
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, true)

                } else {
                    putString(
                        Constants.NAV_FLOW_FROM,
                        Constants.BIOMETRIC_CHANGE
                    )
                    putBoolean(
                        Constants.GO_TO_SUCCESS_PAGE,
                        true
                    )
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, false)
                }

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

    private fun crossingHistoryResponse(resource: Resource<CrossingHistoryApiResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    navigateWithCrossing(it.transactionList?.count ?: 0)
                }
            }

            is Resource.DataError -> {
                startNewActivityByClearingStack(HomeActivityMain::class.java) {
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                }
            }

            else -> {
            }
        }
    }

    private fun navigateWithCrossing(count: Int) {


        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
        intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)
        intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
        intent.putExtra(Constants.CROSSINGCOUNT, count.toString())
        intent.putExtra(Constants.PERSONALDATA, personalInformation)


        intent.putExtra(
            Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
        )
        startActivity(intent)

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

}