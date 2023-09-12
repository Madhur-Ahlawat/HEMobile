package com.conduent.nationalhighways.ui.bottomnav

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModelItem
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.auth.suspended.AccountSuspendReOpenFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.DeletePaymentMethodSuccessFragment
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.NewCardSuccessScreenFragment
import com.conduent.nationalhighways.utils.DateUtils
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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var personalInformation: PersonalInformation? = null
    @Inject
    lateinit var api: ApiService
    var dataBinding: ActivityHomeMainBinding? = null
    private lateinit var navController: NavController
    private var loader: LoaderDialog? = null
    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    var from: String = ""

    companion object {
        var dateRangeModel: PaymentDateRangeModel? = null
        var accountDetailsData: AccountResponse? = null
        var crossing: TransactionData? = null
        var checkedCrossing: CheckedCrossingRecentTransactionsResponseModelItem? = null
        var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
        var paymentHistoryListDataCheckedCrossings: MutableList<CheckedCrossingRecentTransactionsResponseModelItem?> = ArrayList()
    }

    fun showHideToolbar(isShown: Boolean) {
        if (isShown) dataBinding?.idToolBarLyt?.visible() else dataBinding?.idToolBarLyt?.gone()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding?.root)
        setView()
    }

    private fun initLoaderDialog() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }
    fun viewAllTransactions(){
        dataBinding?.apply {
            bottomNavigationView.setActiveNavigationIndex(1)
        }
    }
    private fun getDashBoardAllData() {
        loader?.show(supportFragmentManager, Constants.LOADER_DIALOG)
        val request = CrossingHistoryRequest(
            startIndex = 1,
            count = 5,
            transactionType = Constants.ALL_TRANSACTION,
            searchDate = Constants.TRANSACTION_DATE,
            startDate = DateUtils.lastPriorDate(-90) ?: "", //"11/01/2021" mm/dd/yyyy
            endDate = DateUtils.currentDate() ?: "" //"11/30/2021" mm/dd/yyyy
        )
        Log.e("XJ220", Gson().toJson(request))
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
        Log.e("TAG", "setView: from --> " + from)

        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController
        dataBinding?.titleTxt?.text = getString(R.string.dashboard)
        dataBinding?.idToolBarLyt?.gone()
        dataBinding?.backButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (from == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
            dataBinding?.idToolBarLyt?.visible()
            navController.popBackStack(R.id.bottom_navigation_graph, true)
            dataBinding?.backButton?.gone()
            dataBinding?.titleTxt?.text =
                getString(R.string.str_raise_new_enquiry)
            val bundle: Bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_FROM, from)
            navController.navigate(R.id.enquiryCategoryFragment, bundle)
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
            from = ""

        } else {
            dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        }


        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {

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
                Log.e("TAG", "onDestinationChanged: displayName --> " + destination.displayName)
                if (destination.id == R.id.enquirySuccessFragment) {
                    dataBinding?.backButton?.gone()
                } else {
                    dataBinding?.backButton?.visible()
                }

            }
        })

        dataBinding?.bottomNavigationView?.setOnNavigationItemChangedListener(
            object : OnNavigationItemChangeListener {
                override fun onNavigationItemChanged(
                    navigationItem: BottomNavigationView.NavigationItem
                ) {

                    when (navigationItem.position) {
                        0 -> {
                            if (navController.currentDestination?.id != R.id.dashBoardFragment) {
                                dashboardClick()
                            }
                        }

                        1 -> {
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
        if(!this::navController.isInitialized){
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
    }
    private fun handleAccountDetailsResponse(status: Resource<AccountResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation

                status.data?.apply {

                    accountDetailsData = this
                    sessionManager.saveAccountStatus(accountInformation?.status?:"")
                    sessionManager.saveName(personalInformation?.customerName?:"")
                    sessionManager.saveZipCode(personalInformation?.zipCode?:"")
                    sessionManager.savePhoneNumber(personalInformation?.phoneNumber?:"")
                    sessionManager.saveAccountNumber(accountInformation?.number!!)
                    (applicationContext as BaseApplication).setAccountSavedData(
                        this
                    )
                    dashboardViewModel?.setAccountType(this)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(dataBinding?.root, status.errorMsg)
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
        sessionManager.clearAll()
        Utils.sessionExpired(this)
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
                if (fragment is DeletePaymentMethodSuccessFragment || fragment is AccountSuspendReOpenFragment || fragment is NewCardSuccessScreenFragment) {

                } else {
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

    fun redirectToDashBoardFragment() {
        Log.e("TAG", "redirectToDashBoardFragment: ")
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        dashboardClick()
    }

    fun redirectToAccountFragment() {
        Log.e("TAG", "redirectToAccountFragment: ")
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
        accountFragmentClick()
    }
}