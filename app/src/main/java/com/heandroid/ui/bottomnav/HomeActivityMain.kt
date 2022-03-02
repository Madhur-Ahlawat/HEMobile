package com.heandroid.ui.bottomnav

import android.view.View
import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.ui.customviews.BottomNavigationView
import com.heandroid.databinding.ActivityHomeMainBinding
import com.heandroid.listener.OnNavigationItemChangeListener
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Utils
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivityMain : BaseActivity<ActivityHomeMainBinding>(),LogoutListener {

    private lateinit var dataBinding: ActivityHomeMainBinding

    private fun setView() {
        dataBinding.idToolBarLyt.btnLogin.visibility = View.GONE

        dataBinding.bottomNavigationView.setActiveNavigationIndex(0)
        dataBinding.bottomNavigationView.setOnNavigationItemChangedListener(object :
            OnNavigationItemChangeListener {
            override fun onNavigationItemChanged(navigationItem: BottomNavigationView.NavigationItem) {

                when (navigationItem.position) {

                    0 -> {
                        dataBinding.fragmentContainerView.findNavController()
                            .navigate(R.id.dashBoardFragment)
                    }
                    1 -> {
                        dataBinding.fragmentContainerView.findNavController()
                            .navigate(R.id.vehicleFragment)
                    }
                    2 -> {
                        dataBinding.fragmentContainerView.findNavController()
                            .navigate(R.id.notificationFragment)
                    }
                    3 -> {
                        dataBinding.fragmentContainerView.findNavController()
                            .navigate(R.id.accountFragment)
                    }

                }
            }
        })

    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        dataBinding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        setView()

    }


    override fun onStart() {
        super.onStart()
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        Utils.sessionExpired(this)
    }
}