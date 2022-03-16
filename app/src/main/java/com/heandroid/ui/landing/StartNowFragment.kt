package com.heandroid.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.databinding.FragmentStartNowBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.setRightButtonText
import com.heandroid.utils.extn.visible

class StartNowFragment : BaseFragment<FragmentStartNowBinding>(), View.OnClickListener {

    private var screenType: String = ""


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStartNowBinding {
        return FragmentStartNowBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
        toolbar.findViewById<TextView>(R.id.btn_login).visible()
        requireActivity().setRightButtonText(getString(R.string.login))

    }

    override fun init() {

    }

    override fun initCtrl() {
        binding.apply {
            tvAboutService.setOnClickListener(this@StartNowFragment)
            rlAboutService.setOnClickListener(this@StartNowFragment)
            tvCrossingServiceUpdates.setOnClickListener(this@StartNowFragment)
            rlCrossingServiceUpdate.setOnClickListener(this@StartNowFragment)
            rlContactDartCharge.setOnClickListener(this@StartNowFragment)
            tvContactDartCharge.setOnClickListener(this@StartNowFragment)
            btnStartNow.setOnClickListener(this@StartNowFragment)
        }
    }

    override fun observer() {

    }

    override fun onClick(v: View?) {

        v?.let {
            when (v.id) {
                R.id.tv_about_service,
                R.id.rl_about_service -> {
                    screenType = Constants.ABOUT_SERVICE
                    startServicesActivity()
                }

                R.id.tv_contact_dart_charge,
                R.id.rl_contact_dart_charge -> {
                    screenType = Constants.CONTACT_DART_CHARGES
                    startContactDartChargeActivity()
                }

                R.id.tv_crossing_service_updates,
                R.id.rl_crossing_service_update -> {

                    screenType = Constants.CROSSING_SERVICE_UPDATE
                    startServicesActivity()

                }

                R.id.btn_start_now -> {
                    // navigate to create account user and make one off payment  screen fragment
                    (requireActivity() as LandingActivity).openLandingFragment()

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