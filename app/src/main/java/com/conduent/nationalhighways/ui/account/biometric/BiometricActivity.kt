package com.conduent.nationalhighways.ui.account.biometric


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityBiometricBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BiometricActivity : BaseActivity<ActivityBiometricBinding>(), View.OnClickListener,
    LogoutListener {

    lateinit var binding: ActivityBiometricBinding
    private var twoFA: Boolean = false
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private val toggleDelay: Long = 200

    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private var isScreenLaunchedBefore: Boolean = false
    private var isAuthenticaed: Boolean = false
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    var navFlowFrom: String = ""
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
        if(navFlowFrom.equals(Constants.TWOFA) || navFlowFrom.equals(Constants.LOGIN)|| navFlowFrom.equals(Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS)){
            binding.toolBarLyt.titleTxt.text = getString(R.string.biometrics)
            binding.toolBarLyt.backButton.gone()
            binding.biometricCancel.visible()
        }
        else{
            binding.toolBarLyt.titleTxt.text = getString(R.string.str_profile_biometrics)
            binding.toolBarLyt.backButton.visible()
            binding.biometricCancel.gone()

        }
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        twoFA = intent.getBooleanExtra(Constants.TWOFA, false)


        binding.apply {
            toolBarLyt.backButton.setOnClickListener(this@BiometricActivity)
            btnSave.setOnClickListener(this@BiometricActivity)
            biometricCancel.setOnClickListener(this@BiometricActivity)
        }

        initBiometric(this)

        binding.switchFingerprintLogin.isChecked = sessionManager.fetchTouchIdEnabled()
        if (sessionManager.fetchTouchIdEnabled()) {
            binding.biometricCancel.gone()
        }
        binding.switchFingerprintLogin.setOnCheckedChangeListener { _, isChecked ->
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
                if(sessionManager.fetchTouchIdEnabled()){
                    binding.btnSave.isEnabled = true
                }
                else{
                    binding.btnSave.isEnabled = false
                }
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

    private fun checkBiometricStatus() {
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
                displayAccountSettingsDialog()


            }

            BiometricManager.BIOMETRIC_SUCCESS -> {
                displayFingerPrintPopup()
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
        observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
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
                    isAuthenticaed = false
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticaed = true
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
                if (binding.switchFingerprintLogin.isChecked && isAuthenticaed) {
                    sessionManager.saveTouchIdEnabled(true)
                    goToHomeActivity()
                } else if (!binding.switchFingerprintLogin.isChecked) {
                    sessionManager.saveTouchIdEnabled(false)
                    isAuthenticaed = false
                    goToHomeActivity()
                }

            }

            R.id.biometric_cancel -> {

                goToHomeActivity()

            }
        }
    }


    private fun goToHomeActivity() {
        startNewActivityByClearingStack(HomeActivityMain::class.java) {
            if (navFlowFrom.equals(Constants.TWOFA)) {
                putString(
                    Constants.NAV_FLOW_FROM,
                    navFlowFrom
                )
                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
            } else if(navFlowFrom.equals(Constants.LOGIN)){

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


                    val intent = Intent(this, AuthActivity::class.java)
                    intent.putExtra(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                    intent.putExtra(Constants.NAV_FLOW_FROM, navFlowFrom)
                    intent.putExtra(Constants.CROSSINGCOUNT, "")
                    intent.putExtra(Constants.PERSONALDATA, personalInformation)
                    intent.putExtra(Constants.ACCOUNTINFORMATION, accountInformation)
                    intent.putExtra(
                        Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
                    )
                    startActivity(intent)


                } else {
                    crossingHistoryApi()
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
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.transactionList != null) {
                        navigateWithCrossing(it.transactionList.count ?: 0)

                    } else {
                        startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                        }

                    }

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


        if (count > 0) {


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

        } else {
            startNewActivityByClearingStack(HomeActivityMain::class.java) {
                putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            }
        }


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