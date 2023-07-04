package com.conduent.nationalhighways.ui.auth.controller

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityAuthBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAuthBinding
    public var previousScreen = "home"
    private lateinit var navController: NavController
    private var navFlow:String=""


    @Inject
    lateinit var sessionManager: SessionManager
    override fun initViewBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.getStringExtra(Constants.NAV_FLOW_KEY)!=null){
            navFlow= intent.getStringExtra(Constants.NAV_FLOW_KEY)?:""

        }




        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        previousScreen = if (intent.getIntExtra(
                Constants.FROM_DART_CHARGE_FLOW,
                0
            ) == Constants.DART_CHARGE_FLOW_CODE
        ) {
            "contact dart charge"
        } else {
            "home"

        }


        AdobeAnalytics.setScreenTrack(
            "login",
            "login",
            "english",
            "login",
            previousScreen,
            "login",
            sessionManager.getLoggedInUser()
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_auth)

        val bundle = Bundle()
        if (navFlow==Constants.FORGOT_PASSWORD_FLOW){
            binding.toolBarLyt.titleTxt.text = getString(R.string.forgot_password)

            graph.setStartDestination(R.id.forgotPasswordFragment)
            bundle.putString(Constants.NAV_FLOW_KEY, Constants.FORGOT_PASSWORD_FLOW)

        }else{
            binding.toolBarLyt.titleTxt.text = getString(R.string.str_account_suspended)

            graph.setStartDestination(R.id.accountSuspendedFragment)

        }

        navController = navHostFragment.navController
        navController.setGraph(graph, bundle)

    }

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun observeViewModel() {}

}