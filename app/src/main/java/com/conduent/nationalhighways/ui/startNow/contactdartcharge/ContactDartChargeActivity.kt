package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityContactDartChargeBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactDartChargeActivity : BaseActivity<Any?>(), LogoutListener {

    private lateinit var binding: ActivityContactDartChargeBinding
    var mValue = Constants.FROM_CASES_TO_CASES_VALUE
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityContactDartChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController

        intent?.apply {
            mValue = getIntExtra(Constants.FROM_LOGIN_TO_CASES, Constants.FROM_CASES_TO_CASES_VALUE)
        }

        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_contact_dart_charge)

        when (mValue) {
            Constants.FROM_LOGIN_TO_CASES_VALUE -> {
                oldGraph.setStartDestination(R.id.caseHistoryDartChargeFragment)
            }

            Constants.FROM_ANSWER_TO_CASE_VALUE -> {
                oldGraph.setStartDestination(R.id.dartChargeAccountTypeSelectionFragment)

            }

            else -> {
                oldGraph.setStartDestination(R.id.contactDartCharge)
            }
        }

        AdobeAnalytics.setScreenTrack(
            "dart charge",
            "dart charge",
            "english",
            "dart charge",
            "home",
            "dart charge",
            sessionManager.getLoggedInUser()
        )


        navController.graph = oldGraph

    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.caseHistoryDartChargeFragment) {
            val navHostFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.fragment_container)
            val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
            if (fragment is CaseHistoryDartChargeFragment) {
                if (fragment.isFilterDrawerOpen()) {
                    fragment.closeFilterDrawer()
                    return
                }
            }
        }
        super.onBackPressed()

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
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

}