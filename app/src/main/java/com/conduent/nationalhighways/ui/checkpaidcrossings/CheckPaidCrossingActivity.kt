package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingActivity : BaseActivity<ActivityCreateAccountBinding>() {

    private lateinit var binding: ActivityCreateAccountBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
        AdobeAnalytics.setScreenTrack(
            "check crossings",
            "check crossings",
            "english",
            "check crossings",
            "home",
            "check crossings",
            sessionManager.getLoggedInUser()
        )

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.check_for_previous_crossings)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph_account_creation)
        val navController = navHostFragment.navController
        val destination = R.id.crossingCheck
        navGraph.setStartDestination(destination)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.TRANSFER_CROSSINGS)
        navController.setGraph(navGraph, bundle)

    }

    private fun initCtrl() {
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.clearAll()
    }
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