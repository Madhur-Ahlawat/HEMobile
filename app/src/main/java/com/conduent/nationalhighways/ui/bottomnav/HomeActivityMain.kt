package com.conduent.nationalhighways.ui.bottomnav

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModelItem
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.auth.suspended.AccountSuspendReOpenFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.DeletePaymentMethodSuccessFragment
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.NewCardSuccessScreenFragment
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    var iconColorStates = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ), intArrayOf(
            Color.parseColor("#002E5F"),
            Color.parseColor("#007AFF")
        )
    )
    @Inject
    lateinit var api: ApiService
    private lateinit var navController: NavController
    private var loader: LoaderDialog? = null
    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    var from: String = ""
    var firstTymRedirects: Boolean = false

    companion object {
        var dataBinding: ActivityHomeMainBinding? = null
        var dateRangeModel: PaymentDateRangeModel? = null
        var accountDetailsData: AccountResponse? = null
        var crossing: TransactionData? = null
        var checkedCrossing: CheckedCrossingRecentTransactionsResponseModelItem? = null
        var paymentHistoryListData: MutableList<TransactionData> = mutableListOf()
        var paymentHistoryListDataCheckedCrossings: MutableList<CheckedCrossingRecentTransactionsResponseModelItem?> =
            mutableListOf()

        fun setTitle(title: String) {
            dataBinding?.titleTxt?.text = title
        }
    }

    fun showHideToolbar(isShown: Boolean) {
        if (isShown) dataBinding?.idToolBarLyt?.visible() else dataBinding?.idToolBarLyt?.gone()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding?.root)
        setView()
    }

    fun showLoader() {
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(Constants.LOADER_DIALOG)

        if (existingFragment == null) {
            // Fragment is not added, add it now
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomLoaderDialog)
            loader?.show(fragmentManager, Constants.LOADER_DIALOG)
        }
    }

    fun viewAllTransactions() {
        dataBinding?.apply {
            bottomNavigationView.setActiveNavigationIndex(1)
        }
    }

    private fun getDashBoardAllData() {
        showLoader()
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 5,
            transactionType = Constants.ALL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        dashboardViewModel.getDashboardAllData(request)
    }

    fun hitAPIs(): () -> Unit? {
        getDashBoardAllData()
        dashboardViewModel.getAlertsApi()
        return {}
    }

    private fun setView() {
        if (intent.hasExtra(Constants.NAV_FLOW_FROM)) {
            from = intent.getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
        }

        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController
        dataBinding?.titleTxt?.text = getString(R.string.dashboard)
        dataBinding?.idToolBarLyt?.gone()
        dataBinding?.backButton?.setOnClickListener {
            val currentDestination = navController.currentDestination
            if (currentDestination?.id == R.id.caseEnquiryHistoryListFragment) {
                redirectToAccountFragment()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (from == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            viewModel.enquiryModel.value = EnquiryModel()
            viewModel.edit_enquiryModel.value = EnquiryModel()
            dataBinding?.idToolBarLyt?.visible()
            navController.popBackStack(R.id.bottom_navigation_graph, true)
            dataBinding?.backButton?.gone()
            dataBinding?.titleTxt?.text =
                getString(R.string.str_raise_new_enquiry)
            val bundle: Bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_FROM, from)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
            navController.navigate(R.id.enquiryCategoryFragment, bundle)
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
            from = ""

        } else {
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.closeAccountFragment || destination.id == R.id.accountClosedFragment) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.str_close_account)
            }
            if (destination.id == R.id.changePasswordProfile || destination.id == R.id.changePasswordSuccessProfile) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.str_close_account)
            }
            if (destination.id == R.id.enquiryCategoryFragment
                || destination.id == R.id.enquiryCommentsFragment
                || destination.id == R.id.enquiryContactDetailsFragment
                || destination.id == R.id.enquirySummaryFragment
                || destination.id == R.id.enquirySuccessFragment
            ) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.str_raise_new_enquiry)
            }
            if (destination.id == R.id.caseEnquiryHistoryListFragment
                || destination.id == R.id.casesEnquiryDetailsFragment
            ) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.str_cases_and_enquiries)
            }
            if (destination.id == R.id.enquirySuccessFragment || (destination.id == R.id.enquiryCategoryFragment && from == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS)) {
                dataBinding?.backButton?.gone()
            } else {
                dataBinding?.backButton?.visible()
            }
        }

        dataBinding?.bottomNavigationView?.setOnNavigationItemChangedListener(
            object : OnNavigationItemChangeListener {
                override fun onNavigationItemChanged(
                    navigationItem: BottomNavigationView.NavigationItem
                ) {

                    when (navigationItem.position) {
                        0 -> {
                            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
                            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            if (navController.currentDestination?.id != R.id.dashBoardFragment) {
                                dashboardClick()
                            }
                        }

                        1 -> {
                            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
                            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            if (navController.currentDestination?.id != R.id.crossingHistoryFragment) {
                                dataBinding?.idToolBarLyt?.visible()
                                dataBinding?.titleTxt?.text =
                                    getString(R.string.transactions)
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding?.fragmentContainerView?.findNavController()
                                    ?.navigate(R.id.crossingHistoryFragment)
                            }
                        }

                        2 -> {
                            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
                            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            if (navController.currentDestination?.id != R.id.notificationFragment) {
                                dataBinding?.idToolBarLyt?.visible()
                                dataBinding?.titleTxt?.text =
                                    getString(R.string.str_notifications)
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding?.fragmentContainerView?.findNavController()
                                    ?.navigate(R.id.notificationFragment)
                            }
                        }

                        3 -> {
                            dataBinding?.bottomNavigationView?.get(3)?.setBackgroundTintList(getColorStateList(R.color.hyperlink_blue2))
                            dataBinding?.bottomNavigationView?.get(2)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(1)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            dataBinding?.bottomNavigationView?.get(0)?.setBackgroundTintList(getColorStateList(R.color.new_btn_color))
                            if (navController.currentDestination?.id != R.id.accountFragment) {
                                accountFragmentClick()
                            }
                        }
                    }
                }
            }
        )
    }

    private fun accountFragmentClick() {
        if (!this::navController.isInitialized) {
            navController = (supportFragmentManager.findFragmentById(
                R.id.fragmentContainerView
            ) as NavHostFragment).navController
        }

        dataBinding?.idToolBarLyt?.visible()
        navController.popBackStack(R.id.bottom_navigation_graph, true)
        dataBinding?.fragmentContainerView?.findNavController()
            ?.navigate(R.id.accountFragment)
    }

    private fun dashboardClick() {
        /*
                if(!this::navController.isInitialized){
                    navController = (supportFragmentManager.findFragmentById(
                        R.id.fragmentContainerView
                    ) as NavHostFragment).navController
                }
        */
        dataBinding?.idToolBarLyt?.visible()
        dataBinding?.titleTxt?.text =
            getString(R.string.txt_dashboard)
//        navController.popBackStack(R.id.bottom_navigation_graph, true)
        dataBinding?.fragmentContainerView?.findNavController()
            ?.navigate(R.id.dashBoardFragment)
        getDashBoardAllData()

    }

    override fun observeViewModel() {
        observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetailsResponse)

        if (intent.hasExtra(Constants.FIRST_TYM_REDIRECTS) && intent.getBooleanExtra(
                Constants.FIRST_TYM_REDIRECTS,
                false
            ) == true
        ) {
            callPushNotificationApi()
            observe(webServiceViewModel.pushNotification, ::handlePushNotificationResponse)
        }

    }

    private fun handlePushNotificationResponse(resource: Resource<EmptyApiResponse?>) {
        when (resource) {
            is Resource.Success -> {
                sessionManager.saveNotificationOption(Utils.areNotificationsEnabled(this))
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }
    }

    private fun callPushNotificationApi() {

        sessionManager.getFirebaseToken()?.let { firebaseToken ->
            val request = PushNotificationRequest(
                deviceToken = firebaseToken,
                osName = PushNotificationUtils.getOSName(),
                osVersion = PushNotificationUtils.getOSVersion(),
                appVersion = PushNotificationUtils.getAppVersion(this),
                optInStatus = Utils.getNotificationStatus(this)
            )
            webServiceViewModel.allowPushNotification(request)
        }
    }

    private fun handleAccountDetailsResponse(status: Resource<AccountResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                dashboardViewModel.personalInformationData.value = status.data?.personalInformation
                dashboardViewModel.accountInformationData.value = status.data?.accountInformation
                personalInformation = status.data?.personalInformation

                status.data?.apply {

                    accountDetailsData = this
                    sessionManager.saveAccountStatus(accountInformation?.status ?: "")
                    sessionManager.saveFirstName(personalInformation?.firstName ?: "")
                    sessionManager.saveLastName(personalInformation?.lastName ?: "")
                    sessionManager.saveAccountEmailId(personalInformation?.emailAddress ?: "")
                    sessionManager.saveZipCode(personalInformation?.zipCode ?: "")
                    sessionManager.savePhoneNumber(personalInformation?.phoneNumber ?: "")
                    sessionManager.saveAccountNumber(accountInformation?.number ?: "")
                    sessionManager.saveSmsOption(accountInformation?.smsOption ?: "")
                    if (personalInformation?.phoneCellCountryCode?.isEmpty() == true) {
                        sessionManager.saveUserCountryCode(
                            personalInformation.phoneDayCountryCode ?: ""
                        )
                    } else {
                        sessionManager.saveUserCountryCode(
                            personalInformation?.phoneCellCountryCode ?: ""
                        )
                    }

                    sessionManager.saveUserMobileNumber(personalInformation?.phoneCell ?: "")
                    (applicationContext as BaseApplication).setAccountSavedData(
                        this
                    )
                    dashboardViewModel.setAccountType(this)
                }

                if (status.data?.accountInformation?.accSubType.equals("LRDS")) {
                    startNewActivityByClearingStack(LandingActivity::class.java) {
                        putString(Constants.SHOW_SCREEN, Constants.LRDS_SCREEN)
                    }

                }
            }

            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    ErrorUtil.showError(dataBinding?.root, status.errorMsg)
                }
            }

            else -> {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        BaseApplication.getNewToken(api = api, sessionManager, hitAPIs())
    }

    override fun onStart() {
        super.onStart()
        loadSession()
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
        Utils.sessionExpired(this, this, sessionManager)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is DeletePaymentMethodSuccessFragment ||
                    fragment is AccountSuspendReOpenFragment ||
                    fragment is NewCardSuccessScreenFragment
                ) {

                } else {
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

    fun redirectToDashBoardFragment() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        dashboardClick()
    }

    fun redirectToAccountFragment() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
        accountFragmentClick()
    }

    fun hideBackIcon() {
        dataBinding?.backButton?.gone()
    }

}