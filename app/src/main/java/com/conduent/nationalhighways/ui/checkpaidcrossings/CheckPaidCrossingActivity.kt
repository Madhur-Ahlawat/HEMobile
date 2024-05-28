package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingActivity : BaseActivity<ActivityCreateAccountBinding>(), LogoutListener {

    private lateinit var binding: ActivityCreateAccountBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun observeViewModel() {}

    @Inject
    lateinit var api: ApiService
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
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph_account_creation)
        val navController = navHostFragment.navController
        val destination = R.id.crossingCheck
        navGraph.setStartDestination(destination)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.TRANSFER_CROSSINGS)
        navController.setGraph(navGraph, bundle)

        navController.addOnDestinationChangedListener { _, destination1, _ ->
            if (destination1.id == R.id.findYourVehicleFragment) {
                if (sessionManager.fetchAuthToken()?.isNotEmpty() == true) {
                    binding.toolBarLyt.titleTxt.text =
                        getString(R.string.transfer_remaining_credit)
                } else {
                    binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)
                }
            } else if (destination1.id == R.id.crossingCheck || destination1.id == R.id.crossing_details) {
                binding.toolBarLyt.titleTxt.text = getString(R.string.check_for_previous_crossings)

            }
        }

    }

    private fun initCtrl() {
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is AccountSuccessfullyCreationFragment) {

                } else {
                    onBackPressedDispatcher.onBackPressed()
                }

            }
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
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onDestroy() {
        val touchIdEnabled = sessionManager.fetchTouchIdEnabled()
        val refreshToken = sessionManager.fetchRefreshToken()
        val hasAskedForBiometric = sessionManager.hasAskedForBiometric()
        sessionManager.clearAll()
        sessionManager.saveTouchIdEnabled(touchIdEnabled)
        if (touchIdEnabled) {
            sessionManager.saveRefreshToken(refreshToken ?: "")
            sessionManager.saveHasAskedForBiometric(hasAskedForBiometric)
        }
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

    fun focusToolBarCrossingDetails() {
        val task = Runnable {
            if (binding.toolBarLyt.backButton.isVisible) {
                binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                binding.toolBarLyt.backButton.requestFocus() // Focus on the backButton
            } else {
                binding.toolBarLyt.titleTxt.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                binding.toolBarLyt.titleTxt.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                binding.toolBarLyt.titleTxt.requestFocus() // Focus on the backButton
            }
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }
}