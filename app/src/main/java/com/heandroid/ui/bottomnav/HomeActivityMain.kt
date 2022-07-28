package com.heandroid.ui.bottomnav

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityHomeMainBinding
import com.heandroid.listener.OnNavigationItemChangeListener
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.customviews.BottomNavigationView
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
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
        BaseApplication.INSTANCE.initTimerObject()
        BaseApplication.INSTANCE.startTimerAPi()
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        setView()
    }

    private fun setView() {
        dataBinding.idToolBarLyt.btnLogin.visibility = View.GONE
        dataBinding.bottomNavigationView.setActiveNavigationIndex(0)
        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController

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
                            if (navController.currentDestination?.id != R.id.vehicleFragment) {
                                navController.popBackStack(R.id.bottom_navigation_graph, true)
                                dataBinding.fragmentContainerView.findNavController()
                                    .navigate(R.id.vehicleFragment)
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
        super.onDestroy()
        LogoutUtil.stopLogoutTimer()
        BaseApplication.INSTANCE.stopTimerAPi()
    }
}