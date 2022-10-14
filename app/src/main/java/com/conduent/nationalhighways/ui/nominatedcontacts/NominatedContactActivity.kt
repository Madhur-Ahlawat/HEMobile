package com.conduent.nationalhighways.ui.nominatedcontacts

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityNominatedContactsBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.toolbar
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NominatedContactActivity : BaseActivity<ActivityNominatedContactsBinding>(), LogoutListener {

    private lateinit var binding: ActivityNominatedContactsBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager


    override fun initViewBinding() {
        binding = ActivityNominatedContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar(getString(R.string.str_nominated_contacts))

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_nominated_contacts)

        navController = navHostFragment.navController

        if (intent.getIntExtra("count", 0) > 0)   oldGraph.setStartDestination( R.id.ncListFragment)
        else  oldGraph.setStartDestination(R.id.ncNoListFragment)
        navController.graph = oldGraph
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
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }
}