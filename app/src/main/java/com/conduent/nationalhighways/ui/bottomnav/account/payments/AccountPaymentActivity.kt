package com.conduent.nationalhighways.ui.bottomnav.account.payments

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.PaymentScreenModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityAccountPaymentBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.account.payments.history.AccountPaymentHistoryFragment
import com.conduent.nationalhighways.utils.common.Constants.PAYMENT_HISTORY
import com.conduent.nationalhighways.utils.common.Constants.PAYMENT_METHOD
import com.conduent.nationalhighways.utils.common.Constants.PAYMENT_TOP_UP
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountPaymentActivity : BaseActivity<ActivityAccountPaymentBinding>(), LogoutListener,
    View.OnClickListener {

    private lateinit var binding: ActivityAccountPaymentBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var sessionManager: SessionManager

    override fun observeViewModel() {}

    override fun onStart() {
        super.onStart()
        loadSession()
    }

    override fun initViewBinding() {
        binding = ActivityAccountPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        navController = Navigation.findNavController(this, R.id.fragmentContainerPayment)

        customToolbar(getString(R.string.str_payment))
        binding.model = PaymentScreenModel(history = true, method = false, topUp = false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initCtrl()
    }


    private fun initCtrl() {
        binding.apply {
            toolbar.backButton.setOnClickListener {
                onBackPressed()
            }
            tvPaymentHistory.setOnClickListener(this@AccountPaymentActivity)
            tvPaymentMethod.setOnClickListener(this@AccountPaymentActivity)
            tvTopUp.setOnClickListener(this@AccountPaymentActivity)
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
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvPaymentHistory -> {
                binding.model = PaymentScreenModel(history = true, method = false, topUp = false)
                loadStartDestination(PAYMENT_HISTORY)
            }
            R.id.tvPaymentMethod -> {
                binding.model = PaymentScreenModel(history = false, method = true, topUp = false)
                loadStartDestination(PAYMENT_METHOD)
            }
            R.id.tvTopUp -> {
                binding.model = PaymentScreenModel(history = false, method = false, topUp = true)
                loadStartDestination(PAYMENT_TOP_UP)
            }
        }
    }

    private fun loadStartDestination(type: String?) {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerPayment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_account_payment)
        navController = navHostFragment.navController
        when (type) {
            PAYMENT_HISTORY -> oldGraph.setStartDestination(R.id.accountPaymentHistoryFragment)
            PAYMENT_METHOD -> oldGraph.setStartDestination(R.id.paymentMethodFragment)
            PAYMENT_TOP_UP -> oldGraph.setStartDestination(R.id.paymentTopUpFragment)
        }
        navController.graph = oldGraph

    }

    fun hideTabLayout() {
        binding.tabLikeButtonsLayout.gone()
    }

    fun showTabLayout() {
        binding.tabLikeButtonsLayout.visible()
    }

    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerPayment) as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.accountPaymentHistoryFragment -> {
                    val currentFrag =
                        navHostFragment.childFragmentManager.fragments[0]
                    if (currentFrag is AccountPaymentHistoryFragment) {
                        if (currentFrag.isFilterDrawerOpen()) {
                            currentFrag.closeFilterDrawer()
                        } else {
                            super.onBackPressed()
                        }
                    }
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }
}