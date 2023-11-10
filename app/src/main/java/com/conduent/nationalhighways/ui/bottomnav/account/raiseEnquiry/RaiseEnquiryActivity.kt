package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.content.Context
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityRaiseEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Interceptor
import javax.inject.Inject

@AndroidEntryPoint
class RaiseEnquiryActivity : BaseActivity<ActivityRaiseEnquiryBinding>(), LogoutListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService

    private lateinit var binding: ActivityRaiseEnquiryBinding
    lateinit var navController: NavController
    val viewModel: RaiseNewEnquiryViewModel by viewModels()

    lateinit var listener: NavController.OnDestinationChangedListener
    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = ActivityRaiseEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
    }

    private fun init() {

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
                        binding.toolBarLyt.titleTxt.setText(resources.getString(R.string.str_guidance_and_documents))
                    }
                    R.id.aboutthisserviceFragment -> {
                        binding.toolBarLyt.titleTxt.setText(getString(R.string.about_this_service))
                    }
                    R.id.contactDartChargeFragment -> {
                        binding.toolBarLyt.titleTxt.setText(resources.getString(R.string.str_contact_dart_charge))
                    }
                    R.id.dartChargeAccountTypeEnquiryFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                    }
                    R.id.viewChargesFragment -> {
                        binding.toolBarLyt.titleTxt.setText(getString(R.string.charges_and_fines))
                    }
                    R.id.fragmentTermsAndConditions -> {
                        binding.toolBarLyt.titleTxt.setText("Dart Charge Terms and Conditions")
                    }
                    R.id.generalTermsAndConditionsFragment -> {
                        binding.toolBarLyt.titleTxt.setText("Terms & Conditions")
                    }
                    R.id.paygtermsandconditions -> {
                        binding.toolBarLyt.titleTxt.setText("PAYG Terms & Conditions")
                    }
                    R.id.privacyPolicyFragment -> {
                        binding.toolBarLyt.titleTxt.setText("Privacy Policy")
                    }
                    R.id.otherwaystopayFragment -> {
                        binding.toolBarLyt.titleTxt.setText("Other ways to pay")
                    }
                    R.id.thirdPartySoftwareFragment -> {
                        binding.toolBarLyt.titleTxt.setText("Third party software")
                    }

                    R.id.enquiryStatusFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.enquiry_status)
                    }

                    R.id.casesEnquiryDetailsFragment -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.cases_and_enquiry)
                    }

                    else -> {
                        binding.toolBarLyt.titleTxt.text = getString(R.string.str_raise_new_enquiry)
                    }

                }

                when (destination.id) {

                    R.id.enquirySuccessFragment -> {
                        binding.toolBarLyt.backButton.gone()
                    }

                    else -> {
                        binding.toolBarLyt.backButton.visible()
                    }
                }
            }
        navController.addOnDestinationChangedListener(listener)

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
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onRetryClick(chain: Interceptor.Chain, context: Context) {
        super.onRetryClick(chain, context)
    }

}