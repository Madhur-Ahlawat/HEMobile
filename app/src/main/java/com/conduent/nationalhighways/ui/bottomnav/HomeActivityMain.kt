package com.conduent.nationalhighways.ui.bottomnav

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModelItem
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel.CommunicationPrefsViewModel
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(), LogoutListener {

    private var goToSuccessPage: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()

    @Inject
    lateinit var api: ApiService
    private lateinit var navController: NavController
    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    var from: String = ""
    private var refreshTokenApiCalled: Boolean = false
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()
    var dataBinding: ActivityHomeMainBinding? = null

    lateinit var profileDetailModel: ProfileDetailModel
    private var focusToolBarType: String = ""
    var redirectToPaymentStatus: Boolean = false

    companion object {
        var accountDetailsData: ProfileDetailModel? = null
        var crossing: TransactionData? = null
        var checkedCrossing: CheckedCrossingRecentTransactionsResponseModelItem? = null
        var paymentHistoryListData: MutableList<TransactionData> = mutableListOf()
        var paymentHistoryListDataCheckedCrossings: MutableList<CheckedCrossingRecentTransactionsResponseModelItem?> =
            mutableListOf()
    }

    fun removeBottomBar() {
        dataBinding?.bottomNavigationView?.gone()
    }

    fun changeBottomIconColors(context: Context, pos: Int) {
        if (pos == 0) {
            setSelectedIcon(context, 0)
            setDeselectedIcon(context, 1)
            setDeselectedIcon(context, 2)
            setDeselectedIcon(context, 3)
        }

        if (pos == 1) {
            setSelectedIcon(context, 1)
            setDeselectedIcon(context, 0)
            setDeselectedIcon(context, 2)
            setDeselectedIcon(context, 3)
        }

        if (pos == 2) {
            setSelectedIcon(context, 2)
            setDeselectedIcon(context, 1)
            setDeselectedIcon(context, 0)
            setDeselectedIcon(context, 3)
        }

        if (pos == 3) {
            setSelectedIcon(context, 3)
            setDeselectedIcon(context, 1)
            setDeselectedIcon(context, 2)
            setDeselectedIcon(context, 0)
        }


    }

    private fun setDeselectedIcon(context: Context, i: Int) {
        dataBinding?.bottomNavigationView?.navigationItems?.get(i)?.imageView?.setColorFilter(
            context.resources.getColor(R.color.new_btn_color, null)
        )
        dataBinding?.bottomNavigationView?.navigationItems?.get(i)?.textView?.setTextColor(
            context.resources.getColor(R.color.new_btn_color, null)
        )
    }


    private fun setSelectedIcon(context: Context, i: Int) {
        dataBinding?.bottomNavigationView?.navigationItems?.get(i)?.imageView?.setColorFilter(
            context.resources.getColor(R.color.hyperlink_blue2, null)
        )
        dataBinding?.bottomNavigationView?.navigationItems?.get(i)?.textView?.setTextColor(
            context.resources.getColor(R.color.hyperlink_blue2, null)
        )
    }

    fun setTitle(title: String) {
        dataBinding?.titleTxt?.text = title
    }


    fun showHideToolbar(isShown: Boolean) {
        if (isShown) dataBinding?.idToolBarLyt?.visible() else dataBinding?.idToolBarLyt?.gone()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding?.root)
        setView()
        sessionManager.saveBooleanData(SessionManager.LOGGED_OUT_FROM_DASHBOARD, true)
    }


    fun viewAllTransactions() {
        dataBinding?.apply {
            bottomNavigationView.setActiveNavigationIndex(1)
        }
    }

    private fun getDashBoardAllData() {
        showLoaderDialog()
        dashboardViewModel.getDashboardAllData()
    }

    private fun hitAPIs(): () -> Unit? {
        getDashBoardAllData()
        return {}
    }

    fun getNotificationApi() {
        dashboardViewModel.getAlertsApi()
    }

    private fun setView() {
        if (intent.hasExtra(Constants.NAV_FLOW_FROM)) {
            from = intent.getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
        }

        if (intent.hasExtra(Constants.GO_TO_SUCCESS_PAGE)) {
            goToSuccessPage = intent.getBooleanExtra(Constants.GO_TO_SUCCESS_PAGE, false)
        }

        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController

        dataBinding?.backButton?.setOnClickListener {
            backPressLogic()
        }


        if (from == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            viewModel.enquiryModel.value = EnquiryModel()
            viewModel.edit_enquiryModel.value = EnquiryModel()
            dataBinding?.idToolBarLyt?.visible()
            navController.popBackStack(R.id.bottom_navigation_graph, true)
            dataBinding?.backButton?.gone()
            dataBinding?.titleTxt?.text =
                getString(R.string.str_raise_new_enquiry)
            val bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_FROM, from)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
            navController.navigate(R.id.enquiryCategoryFragment, bundle)
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
            changeBottomIconColors(this@HomeActivityMain, 3)
            from = ""

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_KEY, from)
            bundle.putBoolean(Constants.GO_TO_SUCCESS_PAGE, goToSuccessPage)
            dataBinding?.idToolBarLyt?.gone()
            if (!this::navController.isInitialized) {
                navController = (supportFragmentManager.findFragmentById(
                    R.id.fragmentContainerView
                ) as NavHostFragment).navController
            }
            navController.navigate(R.id.dashBoardFragment, bundle)
            getDashBoardAllData()
            changeBottomIconColors(this@HomeActivityMain, 0)
            focusToolBarHome()
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
            if (destination.id == R.id.casesEnquiryDetailsFragment) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.enquiry_status)
            }
            if (destination.id == R.id.caseEnquiryHistoryListFragment) {
                dataBinding?.idToolBarLyt?.visible()
                dataBinding?.titleTxt?.text =
                    getString(R.string.str_contact_us)
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
                            changeBottomIconColors(this@HomeActivityMain, 0)
                            if (navController.currentDestination?.id != R.id.dashBoardFragment) {
                                dashboardClick()
                            }
                        }

                        1 -> {
                            changeBottomIconColors(this@HomeActivityMain, 1)
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
                            changeBottomIconColors(this@HomeActivityMain, 2)
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
                            changeBottomIconColors(this@HomeActivityMain, 3)
                            if (navController.currentDestination?.id != R.id.accountFragment) {
                                accountFragmentClick()
                            }
                        }

                    }
                }
            }
        )
    }


    fun backPressLogic() {
        val currentDestination = navController.currentDestination
        if ((currentDestination?.id == R.id.notificationFragment) ||
            (currentDestination?.id == R.id.crossingHistoryFragment)
            || (currentDestination?.id == R.id.accountFragment)
        ) {
            Log.e("TAG", "backPressLogic: if ")
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        } else if (currentDestination?.id == R.id.caseEnquiryHistoryListFragment) {
            Log.e("TAG", "backPressLogic: else if")
            redirectToAccountFragment()
        } else {
            Log.e("TAG", "backPressLogic: else ")
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun changeStatusOfRedirectToPayment() {
        redirectToPaymentStatus = false
    }

    fun getRedirectToPayment(): Boolean {
        return redirectToPaymentStatus
    }

    private fun accountFragmentClick(redirectToPayment: Boolean = false) {
        this.redirectToPaymentStatus = redirectToPayment
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
        if (!this::navController.isInitialized) {
            navController = (supportFragmentManager.findFragmentById(
                R.id.fragmentContainerView
            ) as NavHostFragment).navController
        }
        dataBinding?.idToolBarLyt?.visible()
        dataBinding?.titleTxt?.text =
            getString(R.string.txt_dashboard)
        navController.navigate(R.id.dashBoardFragment)
        dataBinding?.fragmentContainerView?.findNavController()
            ?.navigate(R.id.dashBoardFragment)

        getDashBoardAllData()

    }

    override fun observeViewModel() {
        observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetailsResponse)
        observe(dashboardViewModel.getAlertsVal, ::handleAlertResponse)
        if (intent.hasExtra(Constants.FIRST_TYM_REDIRECTS) && intent.getBooleanExtra(
                Constants.FIRST_TYM_REDIRECTS,
                false
            )
        ) {
            callPushNotificationApi()
            observe(webServiceViewModel.pushNotification, ::handlePushNotificationResponse)
        }

        observe(
            communicationPrefsViewModel.getAccountSettingsPrefs,
            ::getCommunicationSettingsPref
        )


    }

    private fun handleAlertResponse(resource: Resource<AlertMessageApiResponse?>?) {
        Log.e("TAG", "handleAlertResponse: ")
        dismissLoaderDialog()

        if (this::profileDetailModel.isInitialized) {
            dashboardViewModel.setAccountType(profileDetailModel)
        }


        when (resource) {
            is Resource.Success -> {
                if (!resource.data?.messageList.isNullOrEmpty()) {
                    val countOfY = resource.data?.messageList?.count { it?.isViewed == "Y" }
                    val countOfN = (resource.data?.messageList?.size ?: 0).minus(countOfY ?: 0)
                    if (countOfN != 0) {
                        dataBinding?.bottomNavigationView?.updateBadgeCount(2, countOfN)
                    }
                }
            }

            is Resource.DataError -> {
                dataBinding?.bottomNavigationView?.updateBadgeCount(2, 0)
            }

            else -> {
                // do nothing
            }
        }
    }

    fun setBadgeCount(countOfN: Int) {
        dataBinding?.bottomNavigationView?.updateBadgeCount(2, countOfN)

    }

    private fun getCommunicationSettingsPref(resource: Resource<ProfileDetailModel?>?) {
        getNotificationApi()
        when (resource) {
            is Resource.Success -> {

                for (i in 0 until resource.data?.accountInformation?.communicationPreferences.orEmpty().size) {
                    val communicationModel =
                        resource.data?.accountInformation?.communicationPreferences?.get(i)

                    if (communicationModel?.category?.lowercase().equals("standard notification")) {
                        val oldSmsOption = communicationModel?.smsFlag?.uppercase()
                        dashboardViewModel.accountInformationData.value?.smsOption = oldSmsOption
                        break
                    }
                }


                dashboardViewModel.communicationPreferenceData.value =
                    resource.data?.accountInformation?.communicationPreferences

            }

            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(dataBinding?.root, resource.errorMsg)
                }
            }

            else -> {
            }
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

    private fun handleAccountDetailsResponse(status: Resource<ProfileDetailModel?>?) {
        Log.e("TAG", "handleAccountDetailsResponse: ")
        refreshTokenApiCalled = false

        communicationPrefsViewModel.getAccountSettingsPrefs()
        when (status) {
            is Resource.Success -> {
                dashboardViewModel.personalInformationData.value = status.data?.personalInformation
                dashboardViewModel.accountInformationData.value = status.data?.accountInformation
                dashboardViewModel.profileDetailModel.value = status.data
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
                    sessionManager.saveAccountType(accountInformation?.accountType)
                    sessionManager.saveSubAccountType(accountInformation?.accSubType)
                    sessionManager.saveSmsOption(accountInformation?.smsOption ?: "")
                    if (personalInformation?.phoneCellCountryCode?.isEmpty() == true) {
                        sessionManager.saveUserCountryCode(
                            personalInformation?.phoneDayCountryCode ?: ""
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
                    profileDetailModel = this
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)) {
                    displaySessionExpireDialog(status.errorModel)
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
        sessionManager.saveBooleanData(SessionManager.SendAuthTokenStatus, true)
        refreshTokenApi()
    }

    fun refreshTokenApi() {
        if (!refreshTokenApiCalled) {
            BaseApplication.getNewToken(api = api, sessionManager, hitAPIs())
        }
        refreshTokenApiCalled = true
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
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    fun redirectToDashBoardFragment() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        dashboardClick()
    }

    fun redirectToAccountFragment() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
        accountFragmentClick()
    }

    fun selectToAccountFragment() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
        accountFragmentClick(true)
    }

    fun hideBackIcon() {
        dataBinding?.backButton?.gone()
    }

    fun requestFocusBackIcon() {
        dataBinding?.backButton?.requestFocus()
    }

    fun focusToolBarHome(type: String = "") {
        if (focusToolBarType == "" || focusToolBarType != type) {
            Log.e("TAG", "focusToolBarHome:@@ $type  focusToolBarType $focusToolBarType")
            dataBinding?.backButton?.requestFocus() // Focus on the backButton
            val task = Runnable {
                if (dataBinding?.backButton?.isVisible == true) {
                    Log.e("TAG", "focusToolBarHome:--> ")
                    dataBinding?.backButton?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                    dataBinding?.backButton?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                } else {
                    Log.e("TAG", "focusToolBarHome:**> ")
                    dataBinding?.titleTxt?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                    dataBinding?.titleTxt?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                }
            }
            Log.e("TAG", "focusToolBarHome:(()) ")
            val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            worker.schedule(task, 1, TimeUnit.SECONDS)
        }
        focusToolBarType = type

    }

}