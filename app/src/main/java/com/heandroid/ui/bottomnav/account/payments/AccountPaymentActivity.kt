package com.heandroid.ui.bottomnav.account.payments

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityAccountPaymentBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.bottomnav.account.payments.accountpaymenthistory.AccountPaymentHistoryFragment
import com.heandroid.ui.bottomnav.account.payments.accountpaymentmethod.AccountPaymentMethodsFragment
import com.heandroid.ui.bottomnav.account.payments.accounttopup.AccountTopUpPaymentFragment
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
        setView()
    }

    private fun setView() {
        binding.toolbar.btnBack.setOnClickListener {
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

        binding.paymentMethodAccount.setOnClickListener {
            makePaymentMethodAccountVisible()

            navHostFragment.childFragmentManager.apply {
                if (fragments[0] !is AccountPaymentMethodsFragment) {
                    when {
                        fragments[0] is AccountPaymentHistoryFragment -> {
                            navController.navigate(R.id.action_accountPaymentHistoryFragment_to_accountPaymentMethodsFragment)
                        }
                        else -> {
                            navController.navigate(R.id.action_accountTopUpPaymentFragment_to_accountPaymentMethodsFragment2)
                        }
                    }
                }
            }

            binding.topUpPayment.setOnClickListener {
                makeTopUpPaymentVisible()
                navHostFragment.childFragmentManager.apply {
                    if (fragments[0] !is AccountTopUpPaymentFragment) {
                        when {
                            fragments[0] is AccountPaymentHistoryFragment -> {
                                navController.navigate(R.id.action_accountPaymentHistoryFragment_to_accountTopUpPaymentFragment2)
                            }
                            else -> {
                                navController.navigate(R.id.action_accountPaymentMethodsFragment_to_accountTopUpPaymentFragment2)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun makePaymentHistoryAccountVisible() {
        binding.apply {
            paymentHistoryAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_selected_bg
            )
            paymentHistoryAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.white
                )
            )

            paymentMethodAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            paymentMethodAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )
            topUpPayment.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            topUpPayment.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )
        }
    }

    private fun makePaymentMethodAccountVisible() {
        binding.apply {
            paymentHistoryAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            paymentHistoryAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )

            paymentMethodAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_selected_bg
            )
            paymentMethodAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.white
                )
            )

            topUpPayment.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            topUpPayment.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )

        }
    }


    private fun makeTopUpPaymentVisible() {
        binding.apply {
            paymentHistoryAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            paymentHistoryAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )

            paymentMethodAccount.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_unselected_bg
            )
            paymentMethodAccount.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.black
                )
            )

            topUpPayment.background = AppCompatResources.getDrawable(
                this@AccountPaymentActivity,
                R.drawable.text_selected_bg
            )
            topUpPayment.setTextColor(
                ContextCompat.getColor(
                    this@AccountPaymentActivity,
                    R.color.white
                )
            )
        }
    }


    override fun observeViewModel() {

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