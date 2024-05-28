package com.conduent.nationalhighways.ui.payment

import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MakeOffPaymentActivity : BaseActivity<Any>(), LogoutListener {
    private var navHostFragment: NavHostFragment? = null
    lateinit var binding: ActivityCreateAccountBinding

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    private var data: CrossingDetailsModelsResponse? = null
    private var lastDestination: Int = 0
    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        data = intent?.getParcelableExtra(Constants.NAV_DATA_KEY)
        setContentView(binding.root)
        init()

        AdobeAnalytics.setScreenTrack(
            "one of payment",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of payment",
            sessionManager.getLoggedInUser()
        )

    }

    fun focusMakeOffToolBar() {
        binding.toolBarLyt.backButton.requestFocus() // Focus on the backButton

        val task = Runnable {
            binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }

    private fun init() {
        NewCreateAccountRequestModel.oneOffVehiclePlateNumber = ""
        NewCreateAccountRequestModel.plateNumber = ""
        binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)

        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val graphInflater = navHostFragment?.navController?.navInflater
        val navGraph = graphInflater?.inflate(R.navigation.nav_graph_account_creation)
        val navController = navHostFragment?.navController
        val destination = R.id.findYourVehicleFragment
        navGraph?.setStartDestination(destination)
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAY_FOR_CROSSINGS)
        if (data != null) {
            bundle.putString(
                Constants.PLATE_NUMBER,
                (data as CrossingDetailsModelsResponse).plateNo
            )
            bundle.putParcelable(Constants.NAV_DATA_KEY, data as CrossingDetailsModelsResponse)
        }
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, true)
        navController?.setGraph(navGraph!!, bundle)


        navController?.addOnDestinationChangedListener { _, destination, _ ->
            lastDestination = destination.id
            if (destination.id == R.id.additionalCrossingsFragment) {
                binding.toolBarLyt.titleTxt.text = getString(R.string.additional_crossings_txt)
            } else {
                binding.toolBarLyt.titleTxt.text = getString(R.string.one_of_payment)

            }
        }
    }

    override fun observeViewModel() {}


    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager, api)
    }

    override fun onPostResume() {
        super.onPostResume()
        binding.toolBarLyt.backButton.requestFocus() // Focus on the backButton
        val task = Runnable {
            if (!binding.toolBarLyt.backButton.isAccessibilityFocused) {
                binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}