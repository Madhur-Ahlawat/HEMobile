package com.conduent.nationalhighways.ui.landing

import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityLandingBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.FAILED_RETRY_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LANDING_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LOGOUT_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.SESSION_TIME_OUT
import com.conduent.nationalhighways.utils.common.Constants.START_NOW_SCREEN
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : BaseActivity<ActivityLandingBinding>() {

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

    private fun initCtrl() {}

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
                START_NOW_SCREEN -> setStartDestination(R.id.landingFragment)
                LANDING_SCREEN -> setStartDestination(R.id.landingFragment)
                LOGOUT_SCREEN -> setStartDestination(R.id.logoutFragment)
                SESSION_TIME_OUT -> setStartDestination(R.id.sessionTimeOutFragment)
                FAILED_RETRY_SCREEN -> setStartDestination(R.id.failedRetryFragment)
            }
        }
    }
}