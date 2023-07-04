package com.conduent.nationalhighways.ui.bottomnav

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityHomeMainBinding
import com.conduent.nationalhighways.listener.OnNavigationItemChangeListener
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.customviews.BottomNavigationView
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.onTextChanged
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var dataBinding: ActivityHomeMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.INSTANCE?.initTimerObject()
        BaseApplication.INSTANCE?.startTimerAPi()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        setView()
    }

    private fun setView() {
        dataBinding.bottomNavigationView.setActiveNavigationIndex(0)
        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController
        dataBinding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        dataBinding.bottomNavigationView.setOnNavigationItemChangedListener(
            object : OnNavigationItemChangeListener {
                override fun onNavigationItemChanged(
                    navigationItem: BottomNavigationView.NavigationItem
                ) {

                    when (navigationItem.position) {
                        0 -> {
                            if (navController.currentDestination?.id != R.id.dashBoardFragment) {
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding.fragmentContainerView.findNavController()
                                    .navigate(R.id.dashBoardFragment)
                            }
                        }
                        1 -> {
                            if (navController.currentDestination?.id != R.id.vehicleHomeListFragment) {
                                dataBinding.idToolBarLyt.titleTxt.text = getString(R.string.vehicle_management)
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding.fragmentContainerView.findNavController()
                                    .navigate(R.id.vehicleHomeListFragment)
                            }
                        }
                        2 -> {
                            if (navController.currentDestination?.id != R.id.notificationFragment) {
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding.fragmentContainerView.findNavController()
                                    .navigate(R.id.notificationFragment)
                            }
                        }
                        3 -> {
                            if (navController.currentDestination?.id != R.id.accountFragment) {
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding.fragmentContainerView.findNavController()
                                    .navigate(R.id.accountFragment)
                            }
                        }
                    }
                }
            }
        )
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

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        BaseApplication.INSTANCE?.stopTimerAPi()
        super.onDestroy()

    }
}