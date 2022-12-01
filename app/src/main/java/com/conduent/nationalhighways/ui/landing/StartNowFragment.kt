package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentStartNowBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.startNow.StartNowBaseActivity
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.conduent.nationalhighways.ui.viewcharges.ViewChargesActivity
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartNowFragment : BaseFragment<FragmentStartNowBinding>(), View.OnClickListener {

    private var screenType: String = ""
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentStartNowBinding.inflate(inflater, container, false)


    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    override fun init() {}

    override fun initCtrl() {
        binding.apply {
            rlAboutService.setOnClickListener(this@StartNowFragment)
            rlCrossingServiceUpdate.setOnClickListener(this@StartNowFragment)
            rlContactDartCharge.setOnClickListener(this@StartNowFragment)
            rlViewCharges.setOnClickListener(this@StartNowFragment)
            btnLogin.setOnClickListener(this@StartNowFragment)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.rl_about_service -> {
                    AdobeAnalytics.setActionTrack(
                        "about service",
                        "dart charge",
                        "dart charge",
                        "englsh",
                        "dart charge",
                        "home",
                        false
                    )

                    screenType = Constants.ABOUT_SERVICE
                    startServicesActivity()
                }

                R.id.rl_contact_dart_charge -> {
                    AdobeAnalytics.setActionTrack(
                        "contact dart charge",
                        "dart charge",
                        "dart charge",
                        "englsh",
                        "dart charge",
                        "home",
                        false
                    )

                    screenType = Constants.CONTACT_DART_CHARGES
                    startContactDartChargeActivity()
                }

                R.id.rl_crossing_service_update -> {
                    AdobeAnalytics.setActionTrack(
                        "crossing service update",
                        "dart charge",
                        "dart charge",
                        "englsh",
                        "dart charge",
                        "home",
                        false
                    )

                    screenType = Constants.CROSSING_SERVICE_UPDATE
                    startServicesActivity()
                }
                R.id.rl_view_charges -> {
                    AdobeAnalytics.setActionTrack(
                        "view charges",
                        "dart charge",
                        "dart charge",
                        "englsh",
                        "dart charge",
                        "home",
                        false
                    )

                    requireActivity().startNormalActivity(ViewChargesActivity::class.java)
                }

                R.id. btnLogin ->{
                    AdobeAnalytics.setActionTrack(
                        "login",
                        "home",
                        "home",
                        "englsh",
                        "home",
                        "splash",
                        sessionManager.getLoggedInUser()
                    )
                    requireActivity().startNormalActivity(
                        AuthActivity::class.java
                    )

                }
            }
        }
    }

    private fun startServicesActivity() {
        Intent(requireActivity(), StartNowBaseActivity::class.java).run {
            putExtra(Constants.SHOW_SCREEN, screenType)
            startActivity(this)
        }

    }

    private fun startContactDartChargeActivity() {
        Intent(requireActivity(), ContactDartChargeActivity::class.java).run {
            startActivity(this)
        }
    }
}