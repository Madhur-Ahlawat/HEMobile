package com.conduent.nationalhighways.ui.payment

import android.os.Bundle
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.databinding.ActivityMakeOffPaymentBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>() {
    lateinit var binding: ActivityCreateAccountBinding
    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()


        AdobeAnalytics.setScreenTrack(
            "one of payment",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of payment",
            sessionManager.getLoggedInUser()
        )

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph_account_creation)
        val navController = navHostFragment.navController
        val destination = R.id.findYourVehicleFragment
        navGraph.setStartDestination(destination)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,Constants.PAY_FOR_CROSSINGS)
        navController.setGraph(navGraph, bundle)

    }

    override fun observeViewModel() {}

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                if (fragment is AccountSuccessfullyCreationFragment){

                }else{
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

}