package com.heandroid.ui.bottomnav.account.payments

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.data.model.payment.PaymentScreenModel
import com.heandroid.databinding.ActivityAccountPaymentBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.bottomnav.account.payments.history.AccountPaymentHistoryFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.PAYMENT_HISTORY
import com.heandroid.utils.common.Constants.PAYMENT_METHOD
import com.heandroid.utils.common.Constants.PAYMENT_TOP_UP
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountPaymentActivity : BaseActivity<ActivityAccountPaymentBinding>(), LogoutListener,
    View.OnClickListener {

    private lateinit var binding: ActivityAccountPaymentBinding
    private lateinit var navController: NavController

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
        sessionManager.clearAll()
        Utils.sessionExpired(this)
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