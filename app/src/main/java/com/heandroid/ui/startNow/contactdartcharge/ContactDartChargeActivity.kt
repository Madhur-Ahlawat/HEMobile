package com.heandroid.ui.startNow.contactdartcharge

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityContactDartChargeBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactDartChargeActivity : BaseActivity<Any?>(), LogoutListener {

    private lateinit var binding: ActivityContactDartChargeBinding
    var mValue = Constants.FROM_CASES_TO_CASES_VALUE
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager : SessionManager

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

        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            oldGraph.setStartDestination(R.id.caseHistoryDartChargeFragment)
        } else {
            oldGraph.setStartDestination(R.id.contactDartCharge)
        }

        navController.graph = oldGraph

    }

    override fun onBackPressed() {
        super.onBackPressed()
/*
        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            finish()
            startNormalActivity(HomeActivityMain::class.java)
        }
*/
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

    private fun loadSession(){
        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            LogoutUtil.stopLogoutTimer()
            LogoutUtil.startLogoutTimer(this)
        }
    }

    override fun onLogout() {
        if (mValue == Constants.FROM_LOGIN_TO_CASES_VALUE) {
            sessionManager.clearAll()
            Utils.sessionExpired(this)
        }
    }

}