package com.heandroid.ui.landing

import android.content.Intent
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.heandroid.R
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : BaseActivity<Any?>() {

    private lateinit var navController: NavController
    private var screenType: String = ""
    private lateinit var binding: ActivityLandingBinding


    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(this, R.id.nav_host_fragment_container)
        screenType = intent?.getStringExtra(Constants.SHOW_SCREEN).toString()
        loadFragment()
        binding.toolBarLyt.btnLogin.setOnClickListener {
           when(screenType)
           {
               Constants.LANDING_SCREEN,
                   Constants.START_NOW_SCREEN->{
                       startLoginActivity()
                   }
               Constants.LOGOUT_SCREEN->{

                   openContactusScreen()
               }

               else->{
                   startLoginActivity()
               }
           }

        }

    }

    private fun loadFragment() {

        var oldGraph = navController.graph.apply {
            when (screenType) {
                Constants.START_NOW_SCREEN -> {
                    startDestination = R.id.startNow
                }
                Constants.LANDING_SCREEN -> {
                    startDestination = R.id.landingFragment
                }
                Constants.LOGOUT_SCREEN -> {
                    startDestination = R.id.logoutFragment
                }
            }
        }

        navController.graph = oldGraph
    }

    private fun openContactusScreen() {

        Intent(this, InProgressActivity::class.java).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }

    }


    fun openLandingFragment() {
        screenType = Constants.LANDING_SCREEN
        loadFragment()
    }

    override fun observeViewModel() {


    }

    private fun startLoginActivity() {

        Intent(this, AuthActivity::class.java).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }

    }
}