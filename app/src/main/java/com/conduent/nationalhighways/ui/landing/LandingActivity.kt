package com.conduent.nationalhighways.ui.landing

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityLandingBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.FAILED_RETRY_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LANDING_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LOGOUT_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.LRDS_SCREEN
import com.conduent.nationalhighways.utils.common.Constants.SESSION_TIME_OUT
import com.conduent.nationalhighways.utils.common.Constants.START_NOW_SCREEN
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LandingActivity : BaseActivity<ActivityLandingBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
//    private var loader: LoaderDialog? = null

    private lateinit var navController: NavController
    private var screenType: String = ""
    val viewModel: WebSiteServiceViewModel by viewModels()
    companion object{
        private lateinit var binding: ActivityLandingBinding

        fun showToolBar(isShown:Boolean){
            if(isShown){
             binding.toolbar.visible()
            }
            else{
                binding.toolbar.gone()
            }
        }
        fun setToolBarTitle(title:String){
                binding.titleTxt.text=title
        }
    }
    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(this, R.id.nav_host_fragment_container)
        screenType = intent?.getStringExtra(Constants.SHOW_SCREEN).toString()
        Logg.logging("landingActivy", "test called $screenType")

        loadFragment(screenType)

        navControllerListener()

        if (screenType == Constants.LRDS_SCREEN) {
            binding.titleTxt.text = resources.getString(R.string.txt_my_account)
        } else {
            binding.titleTxt.text = resources.getString(R.string.failed_problem_with_service)
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun navControllerListener() {
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when (destination.id) {
                    R.id.failedRetryFragment, R.id.serviceUnavailableFragment -> {
                        binding.toolbar.visible()
                    }

                    else -> {
                        binding.toolbar.gone()
                    }
                }

            }
        })

    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
        initCtrl()


    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    private fun initCtrl() {
    }

    override fun observeViewModel() {
    }

    private fun loadFragment(screenType: String) {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val oldGraph = inflater.inflate(R.navigation.nav_graph_landing)

        val bundle: Bundle = Bundle()
        if (intent.extras != null)
            oldGraph.addArgument(
                Constants.TYPE,
                NavArgument.Builder().setDefaultValue(intent.extras).build()
            )

        if (this.screenType == Constants.LRDS_SCREEN) {
            bundle.putString(Constants.SERVICE_TYPE, Constants.LRDS_SCREEN)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        }

        oldGraph.apply {
            when (screenType) {
                START_NOW_SCREEN -> setStartDestination(R.id.landingFragment)
                LANDING_SCREEN -> setStartDestination(R.id.landingFragment)
                LOGOUT_SCREEN -> setStartDestination(R.id.logoutFragment)
                SESSION_TIME_OUT -> setStartDestination(R.id.sessionTimeOutFragment)
                FAILED_RETRY_SCREEN -> setStartDestination(R.id.failedRetryFragment)
                LRDS_SCREEN -> {
                    setStartDestination(R.id.serviceUnavailableFragment)
                }
            }
        }
        navController.setGraph(oldGraph, bundle)

    }
}