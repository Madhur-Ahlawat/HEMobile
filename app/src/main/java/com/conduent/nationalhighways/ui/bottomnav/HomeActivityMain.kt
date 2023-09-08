package com.conduent.nationalhighways.ui.bottomnav

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.auth.suspended.AccountSuspendReOpenFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.DeletePaymentMethodSuccessFragment
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.NewCardSuccessScreenFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    var dataBinding: ActivityHomeMainBinding? = null
    private lateinit var navController: NavController
    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    var from: String = ""

    companion object {
        var dateRangeModel: PaymentDateRangeModel? = null
        var accountDetailsData: AccountResponse? = null
        var crossing: TransactionData? = null
        var paymentHistoryListData: MutableList<TransactionData?> = ArrayList()
    }

    fun showHideToolbar(isShown: Boolean) {
        if (isShown) dataBinding?.idToolBarLyt?.visible() else dataBinding?.idToolBarLyt?.gone()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding?.root)
        setView()
    }

    fun viewAllTransactions() {
        dataBinding?.apply {
            bottomNavigationView?.setActiveNavigationIndex(1)
        }
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
                    Log.e("TAG", "onNavigationItemChanged: position--> " + navigationItem.position)
                    dataBinding?.backButton?.visible()

                    when (navigationItem.position) {
                        0 -> {
                            if (navController.currentDestination?.id != R.id.dashBoardFragment) {
                                dataBinding?.idToolBarLyt?.visible()
                                dataBinding?.titleTxt?.text =
                                    getString(R.string.txt_dashboard)
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding?.fragmentContainerView?.findNavController()
                                    ?.navigate(R.id.dashBoardFragment)
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
                                dataBinding?.idToolBarLyt?.visible()
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding?.fragmentContainerView?.findNavController()
                                    ?.navigate(R.id.accountFragment)
                            }
                        }
                    }
                }
            }
        )

    }

    override fun observeViewModel() {}

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
        navController.navigate(R.id.action_enquiryCategoryFragment_to_dashBoardFragment)
    }

    fun redirectToAccountFragment() {
        Log.e("TAG", "redirectToAccountFragment: ", )
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(3)
        navController.navigate(R.id.action_enquiryCategoryFragment_to_accountFragment)
    }
}