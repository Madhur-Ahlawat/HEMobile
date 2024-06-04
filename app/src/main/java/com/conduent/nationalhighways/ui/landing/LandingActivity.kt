package com.conduent.nationalhighways.ui.landing

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavArgument
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
import com.conduent.nationalhighways.utils.common.Constants.SERVER_ERROR
import com.conduent.nationalhighways.utils.common.Constants.SESSION_TIME_OUT
import com.conduent.nationalhighways.utils.common.Constants.START_NOW_SCREEN
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : BaseActivity<ActivityLandingBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    private var navFlowFrom: String = ""
    private var plateNumber: String = ""
    private var email: String = ""
    private var mobileNumber: String = ""
    private var countryCode: String = ""
    private var screenType: String = ""
    val viewModel: WebSiteServiceViewModel by viewModels()
    private lateinit var binding: ActivityLandingBinding


    fun showToolBar(isShown: Boolean) {
        if (isShown) {
            binding.toolbar.visible()
        } else {
            binding.toolbar.gone()
        }
    }

    fun setToolBarTitle(title: String) {
        binding.titleTxt.text = title
    }

    fun setBackIcon(status: Int) {
        binding.btnBack.visibility = status
    }


    override fun initViewBinding() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenType = intent?.getStringExtra(Constants.SHOW_SCREEN).toString()

        if (intent?.hasExtra(Constants.NAV_FLOW_FROM) == true) {
            navFlowFrom = intent.getStringExtra(Constants.NAV_FLOW_FROM) ?: ""
        }
        if (intent?.hasExtra(Constants.PLATE_NUMBER) == true) {
            plateNumber = intent.getStringExtra(Constants.PLATE_NUMBER) ?: ""
        }
        if (intent?.hasExtra(Constants.EMAIL) == true) {
            email = intent.getStringExtra(Constants.EMAIL) ?: ""
        }
        if (intent?.hasExtra(Constants.MOBILE_NUMBER) == true) {
            mobileNumber = intent.getStringExtra(Constants.MOBILE_NUMBER) ?: ""
        }
        if (intent?.hasExtra(Constants.COUNTRY_TYPE) == true) {
            countryCode = intent.getStringExtra(Constants.COUNTRY_TYPE) ?: ""
        }

        loadFragment(screenType)

        when (screenType) {
            LRDS_SCREEN -> {
                binding.titleTxt.text = resources.getString(R.string.txt_my_account)
                setBackIcon(View.GONE)
            }

            LOGOUT_SCREEN, SESSION_TIME_OUT -> {
                binding.titleTxt.text = resources.getString(R.string.str_signed_out)
                setBackIcon(View.GONE)
            }

            SERVER_ERROR, FAILED_RETRY_SCREEN -> {
                binding.titleTxt.text = resources.getString(R.string.failed_problem_with_service)
                setBackIcon(View.GONE)
            }

            else -> {
                binding.titleTxt.text = resources.getString(R.string.failed_problem_with_service)
                setBackIcon(View.VISIBLE)
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        backClickListener()
        sessionManager.saveBooleanData(SessionManager.SendAuthTokenStatus, false)
        if (sessionManager.fetchBooleanData(SessionManager.SettingsClick)) {
            sessionManager.saveBooleanData(
                SessionManager.LOCATION_PERMISSION,
                Utils.checkLocationPermission(this)
            )
        }
    }

    private fun backClickListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.btnBack.visibility == View.VISIBLE) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    // Implement your custom back navigation logic
                } else {
                    Utils.vibrate(this@LandingActivity)
                }
            }
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
        initCtrl()
        sessionManager.saveBooleanData(
            SessionManager.NOTIFICATION_PERMISSION,
            Utils.areNotificationsEnabled(this)
        )

        Log.e("TAG", "onResume: fontScale " + getResources().configuration.fontScale)

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

        val bundle = Bundle()
        if (intent.extras != null)
            oldGraph.addArgument(
                Constants.TYPE,
                NavArgument.Builder().setDefaultValue(intent.extras).build()
            )

        if (this.screenType == LRDS_SCREEN || this.screenType == SERVER_ERROR) {
            bundle.putString(Constants.SERVICE_TYPE, this.screenType)
            bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
        }
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        bundle.putString(Constants.PLATE_NUMBER, plateNumber)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.MOBILE_NUMBER, mobileNumber)
        bundle.putString(Constants.COUNTRY_TYPE, countryCode)
        oldGraph.apply {
            when (screenType) {
                START_NOW_SCREEN -> setStartDestination(R.id.landingFragment)
                LANDING_SCREEN -> setStartDestination(R.id.landingFragment)
                LOGOUT_SCREEN -> setStartDestination(R.id.logoutFragment)
                SESSION_TIME_OUT -> setStartDestination(R.id.sessionTimeOutFragment)
                FAILED_RETRY_SCREEN -> {
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                    setStartDestination(R.id.failedRetryFragment)
                }

                LRDS_SCREEN, SERVER_ERROR -> {
                    setStartDestination(R.id.serviceUnavailableFragment)
                }
            }
        }
        navHostFragment.navController.setGraph(oldGraph, bundle)
    }

    fun focusToolBarLanding() {
        binding.btnBack.requestFocus()
        val task = Runnable {
            if (binding.btnBack.isVisible) {
                binding.btnBack.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                binding.btnBack.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            } else {
                binding.titleTxt.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                binding.titleTxt.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            }
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }


}


