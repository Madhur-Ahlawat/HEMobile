package com.conduent.nationalhighways.ui.bottomnav

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.accountpayment.CheckedCrossingRecentTransactionsResponseModelItem
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.auth.suspended.AccountSuspendReOpenFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.DeletePaymentMethodSuccessFragment
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.NewCardSuccessScreenFragment
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

    fun viewAllTransactions(){
        dataBinding?.apply {
            bottomNavigationView.setActiveNavigationIndex(1)
        }
    }

    private fun setView() {
        dataBinding?.bottomNavigationView?.setActiveNavigationIndex(0)
        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController

        dataBinding?.titleTxt?.text = getString(R.string.dashboard)
        dataBinding?.idToolBarLyt?.gone()
        dataBinding?.backButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        }

        dataBinding?.bottomNavigationView?.setOnNavigationItemChangedListener(
            object : OnNavigationItemChangeListener {
                override fun onNavigationItemChanged(
                    navigationItem: BottomNavigationView.NavigationItem
                ) {

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
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                if (fragment is DeletePaymentMethodSuccessFragment||fragment is AccountSuspendReOpenFragment||fragment is NewCardSuccessScreenFragment){

                }else{
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }
}