package com.heandroid.ui.bottomnav.account.payments

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityAccountPaymentBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountPaymentActivity : BaseActivity<ActivityAccountPaymentBinding>(), LogoutListener {

    private lateinit var binding: ActivityAccountPaymentBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityAccountPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_payment))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerPayment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_account_payment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.payment_method_account -> {
                    binding.chipButtonPayment = true
                    binding.tabLikeButtonsLayout.visible()
                }
                R.id.payment_method_account -> {
                    binding.chipButtonPayment = false
                    binding.tabLikeButtonsLayout.visible()
                }

                else -> {
                    binding.tabLikeButtonsLayout.gone()
                }
            }

        }

    }

    override fun observeViewModel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStart() {
        super.onStart()
        loadsession()
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


}