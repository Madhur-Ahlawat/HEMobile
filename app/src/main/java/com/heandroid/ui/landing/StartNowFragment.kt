package com.heandroid.ui.landing

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentStartNowBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.heandroid.ui.viewcharges.ViewChargesActivity
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartNowFragment : BaseFragment<FragmentStartNowBinding>(), View.OnClickListener {

    private var screenType: String = ""

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
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.rl_about_service -> {
                    screenType = Constants.ABOUT_SERVICE
                    startServicesActivity()
                }

                R.id.rl_contact_dart_charge -> {
                    screenType = Constants.CONTACT_DART_CHARGES
                    startContactDartChargeActivity()
                }

                R.id.rl_crossing_service_update -> {
                    screenType = Constants.CROSSING_SERVICE_UPDATE
                    startServicesActivity()
                }
                R.id.rl_view_charges -> {
                    requireActivity().startNormalActivity(ViewChargesActivity::class.java)
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