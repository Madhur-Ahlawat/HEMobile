package com.heandroid.ui.bottomnav.account.payments

import android.view.View
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.data.model.payment.PaymentScreenModel
import com.heandroid.databinding.ActivityAccountPaymentBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.PAYMENT_HISTORY
import com.heandroid.utils.common.Constants.PAYMENT_METHOD
import com.heandroid.utils.common.Constants.PAYMENT_TOP_UP
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.customToolbar
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountPaymentActivity : BaseActivity<ActivityAccountPaymentBinding>(), LogoutListener, View.OnClickListener {

    private lateinit var binding: ActivityAccountPaymentBinding

    @Inject
    lateinit var sessionManager: SessionManager




    override fun observeViewModel() {

    }

    override fun onStart() {
        super.onStart()
        loadsession()
    }

    override fun initViewBinding() {
        binding = ActivityAccountPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customToolbar(getString(R.string.str_payment))
        setView()
        binding.model= PaymentScreenModel(history = true, method = false, topUp = false)
        toolbar(getString(R.string.str_payment))
        initCtrl()
    }

    private fun setView() {
        binding.toolbar.backButton.setOnClickListener {
            onBackPressed()
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerPayment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_account_payment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.payment_history_account -> {
                    makePaymentHistoryAccountVisible()
                    binding.tabLikeButtonsLayout.visible()
                }
                R.id.payment_method_account -> {
                    makePaymentMethodAccountVisible()
                    binding.tabLikeButtonsLayout.visible()
                }
                R.id.top_up_payment -> {
                    makeTopUpPaymentVisible()
                    binding.tabLikeButtonsLayout.visible()
                }

                R.id.accountPaymentHistoryFilterFragment -> {
                    binding.tabLikeButtonsLayout.gone()
                }

                R.id.accountPaymentHistoryItemDetailFragment -> {
                    binding.tabLikeButtonsLayout.gone()
                }
                else -> {
                    binding.tabLikeButtonsLayout.visible()
                }
            }
        }

        binding.paymentHistoryAccount.setOnClickListener {
            makePaymentHistoryAccountVisible()
            navHostFragment.childFragmentManager.apply {
                if (fragments[0] !is AccountPaymentHistoryFragment) {
                    when {
                        fragments[0] is AccountPaymentMethodsFragment -> {
                            navController.navigate(R.id.action_accountPaymentMethodsFragment_to_accountPaymentHistoryFragment)
                        }
                        else -> {
                            navController.navigate(R.id.action_accountTopUpPaymentFragment_to_accountPaymentHistoryFragment2)
                        }
                    }
                }
            }
        }

    private fun initCtrl() {
        binding.apply {
            tvPaymentHistory.setOnClickListener(this@AccountPaymentActivity)
            tvPaymentMethod.setOnClickListener(this@AccountPaymentActivity)
            tvTopUp.setOnClickListener(this@AccountPaymentActivity)
        }
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        loadsession()
    }


    private fun loadsession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvPaymentHistory -> { binding.model= PaymentScreenModel(history = true, method = false, topUp = false)
                                       loadStartDestination(PAYMENT_HISTORY)  }
            R.id.tvPaymentMethod  -> { binding.model=PaymentScreenModel(history = false, method = true, topUp = false)
                                       loadStartDestination(PAYMENT_METHOD)  }
            R.id.tvTopUp ->         { binding.model=PaymentScreenModel(history = false, method = false, topUp = true)
                                      loadStartDestination(PAYMENT_TOP_UP)  }
        }
    }

    private fun loadStartDestination(type: String?) {
       val navController= Navigation.findNavController(this,R.id.fragmentContainerPayment)
       val oldGraph= navController.graph
        when(type){
            PAYMENT_HISTORY -> oldGraph.startDestination = R.id.accountPaymentHistoryFragment
            PAYMENT_METHOD ->  oldGraph.startDestination = R.id.paymentMethodFragment
            PAYMENT_TOP_UP ->  oldGraph.startDestination  = R.id.paymentTopUpFragment
       }
        navController.graph = oldGraph
    }

}