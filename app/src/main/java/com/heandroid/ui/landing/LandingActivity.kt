package com.heandroid.ui.landing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.LANDING_SCREEN
import com.heandroid.utils.common.Constants.LOGOUT_SCREEN
import com.heandroid.utils.common.Constants.SESSION_TIME_OUT
import com.heandroid.utils.common.Constants.START_NOW_SCREEN
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.startNormalActivity
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : BaseActivity<Any?>() {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var navController: NavController
    private var screenType: String = ""
    private lateinit var binding: ActivityLandingBinding


    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(this, R.id.nav_host_fragment_container)
        screenType = intent?.getStringExtra(Constants.SHOW_SCREEN).toString()

        if(checkSession()) loadFragment()

        initCtrl()
    }

    private fun initCtrl() {
        binding.toolBarLyt.btnLogin.setOnClickListener {
            when(screenType)
            {
                LANDING_SCREEN, START_NOW_SCREEN->{ startIntent(AuthActivity::class.java) }
                LOGOUT_SCREEN->{ startIntent(InProgressActivity::class.java) }
                else ->{ startIntent(AuthActivity::class.java) }
            }

        }
    }

    override fun observeViewModel() {}

    private fun loadFragment() {
        navController.setGraph(R.navigation.nav_graph_landing,intent.extras)
        val oldGraph = navController.graph
        if(intent.extras!=null)
        oldGraph.addArgument(Constants.TYPE, NavArgument.Builder().setDefaultValue(intent.extras).build())
        navController.graph = oldGraph.apply {
            when (screenType) {
                START_NOW_SCREEN -> startDestination = R.id.startNow
                LANDING_SCREEN -> startDestination = R.id.landingFragment
                LOGOUT_SCREEN -> startDestination = R.id.logoutFragment
                SESSION_TIME_OUT-> startDestination = R.id.sessionTimeOutFragment
            }
        }

    }


    fun openLandingFragment() {
        screenType = LANDING_SCREEN
        loadFragment()
    }

    private fun checkSession() : Boolean{
        return if(sessionManager.fetchAuthToken()!=null){
            Log.e("current time",Calendar.getInstance().timeInMillis.toString())
            Log.e("save time",sessionManager.getSessionTime().toString())
            if(Calendar.getInstance().timeInMillis-sessionManager.getSessionTime()<LogoutUtil.LOGOUT_TIME){
                startActivity(Intent(this, HomeActivityMain::class.java)
                              .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))

                false
            }else{
                sessionManager.clearAll()
                true
            }

        }else true
    }

    private fun <A: Activity> startIntent(activity: Class<A>) {
        Intent(this, activity).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

}