package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.view.accessibility.AccessibilityEvent
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityRaiseEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class RaiseEnquiryActivity : BaseActivity<ActivityRaiseEnquiryBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService

    lateinit var navController: NavController
    val viewModel: RaiseNewEnquiryViewModel by viewModels()
    lateinit var listener: NavController.OnDestinationChangedListener
    override fun observeViewModel() {

    }

    companion object{
        private lateinit var binding: ActivityRaiseEnquiryBinding

        fun setToolBarTitle(title: String) {
            binding.toolBarLyt.titleTxt.text = title
        }
        fun setBackIcon(status: Int) {
            binding.toolBarLyt.backButton.visibility=status
        }
    }

    override fun initViewBinding() {
        binding = ActivityRaiseEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
    }

    private fun init() {
        viewModel.apiState.value=intent.getStringExtra(Constants.API_STATE)?:""
        viewModel.apiEndTime.value=intent.getStringExtra(Constants.API_END_TIME)?:""
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_raise_enquiry_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                // Handle navigation events here
                when (destination.id) {

                    R.id.guidanceDocumentsFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_dart_charge_forms_guidance)
                    }

                    R.id.aboutthisserviceFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.about_this_service)
                    }

                    R.id.contactDartChargeFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_contact_dart_charge)
                    }

                    R.id.dartChargeAccountTypeEnquiryFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                    }

                    R.id.viewChargesFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.charges_and_fines)
                    }

                    R.id.fragmentTermsAndConditions -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_dart_charge_terms_conditions)
                    }

                    R.id.generalTermsAndConditionsFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_terms_conditions)
                    }

                    R.id.paygtermsandconditions -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_payg_terms_conditions)
                    }

                    R.id.privacyPolicyFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_privacy_policy)
                    }

                    R.id.otherwaystopayFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_other_ways_topay)
                    }

                    R.id.thirdPartySoftwareFragment -> {
                        binding.toolBarLyt.titleTxt.text = resources.getString(R.string.str_third_party_software)
                    }

                    R.id.enquiryStatusFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.enquiry_status)
                    }

                    R.id.casesEnquiryDetailsFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.enquiry_status)
                    }
                    R.id.serviceUnavailableFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_service_is_unavailable)
                    }

                    else -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                    }

                }

                when (destination.id) {
                    R.id.enquirySuccessFragment or R.id.serviceUnavailableFragment -> {
                        binding.toolBarLyt.backButton.gone()
                    }

                    else -> {
                        binding.toolBarLyt.backButton.visible()
                    }
                }
            }
        navController.addOnDestinationChangedListener(listener)
        AdobeAnalytics.setScreenTrack(
            "Raise Enquiry",
            "login",
            "english",
            "login", "",
            "login",
            sessionManager.getLoggedInUser()
        )
    }

    private fun initCtrl() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::navController.isInitialized) {
            navController.removeOnDestinationChangedListener(listener)
        }
        LogoutUtil.stopLogoutTimer()
    }

    fun hideBackIcon() {
        binding.toolBarLyt.backButton.gone()
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
        LogoutUtil.stopLogoutTimer()
//        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }
    fun focusToolBarRaiseEnquiry() {
        binding.toolBarLyt.backButton.requestFocus()
        binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        val task = Runnable {
            binding.toolBarLyt.backButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        worker.schedule(task, 1, TimeUnit.SECONDS)
    }
}