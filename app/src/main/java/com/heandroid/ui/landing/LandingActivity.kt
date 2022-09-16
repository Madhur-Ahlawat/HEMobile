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
import com.adobe.marketing.mobile.MobileCore
import com.heandroid.R
import com.heandroid.databinding.ActivityLandingBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.utils.common.AdobeAnalytics
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.FAILED_RETRY_SCREEN
import com.heandroid.utils.common.Constants.LANDING_SCREEN
import com.heandroid.utils.common.Constants.LOGOUT_SCREEN
import com.heandroid.utils.common.Constants.SESSION_TIME_OUT
import com.heandroid.utils.common.Constants.START_NOW_SCREEN
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.startNormalActivity
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
        loadFragment()

        AdobeAnalytics.setScreenTrack(
            "landing",
            "landing",
            "english",
            "landing",
            "splash",
            "landing"
        )
    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
        BaseApplication.INSTANCE?.stopTimerAPi()
        sessionManager.clearAll()
        initCtrl()
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)

    }


    private fun initCtrl() {
        binding.toolBarLyt.btnLogin.setOnClickListener {
            when (screenType) {
                LANDING_SCREEN, START_NOW_SCREEN -> {
                    startIntent(AuthActivity::class.java)

                }
                LOGOUT_SCREEN -> {
                    if (binding.toolBarLyt.btnLogin.text.toString()
                            .trim().contains(getString(R.string.contact_us))
                    ) {
                        startIntent(InProgressActivity::class.java)
                    } else
                        startIntent(AuthActivity::class.java)
                }
                FAILED_RETRY_SCREEN -> {
                    startIntent(AuthActivity::class.java)
                }
                else -> {
                    startIntent(AuthActivity::class.java)
                }
            }
        }
    }

    override fun observeViewModel() {}

    private fun loadFragment() {
        navController.setGraph(R.navigation.nav_graph_landing, intent.extras)
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_landing)

        if (intent.extras != null)
            oldGraph.addArgument(
                Constants.TYPE,
                NavArgument.Builder().setDefaultValue(intent.extras).build()
            )

        navController.graph = oldGraph.apply {
            when (screenType) {
                START_NOW_SCREEN -> setStartDestination(R.id.startNow)
                LANDING_SCREEN -> setStartDestination(R.id.landingFragment)
                LOGOUT_SCREEN -> setStartDestination(R.id.logoutFragment)
                SESSION_TIME_OUT -> setStartDestination(R.id.sessionTimeOutFragment)
                FAILED_RETRY_SCREEN -> setStartDestination(R.id.failedRetryFragment)
            }
        }
    }

    private fun <A : Activity> startIntent(activity: Class<A>) {
        Intent(this, activity).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }
    }

}