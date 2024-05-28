package com.conduent.nationalhighways.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentGpsSettingsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GpsSettingsFragment : BaseFragment<FragmentGpsSettingsBinding>() {
    @Inject
    lateinit var sessionManager:SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGpsSettingsBinding = FragmentGpsSettingsBinding.inflate(inflater, container, false)


    override fun init() {
        showToolBar(true)
        setToolBarTitle(resources.getString(R.string.str_register_to_receive_notifications))
    }

    override fun initCtrl() {
        binding.btnGotoSettings.setOnClickListener {
            Utils.openAppSettings(requireActivity())
        }
        binding.btnOptout.setOnClickListener {
            sessionManager.saveBooleanData(
                SessionManager.FOREGROUND_LOCATION_SHOWN,
                true
            )
            val bundle=Bundle()
            bundle.putBoolean(Constants.GpsSettings,true)
            sessionManager.saveBooleanData(SessionManager.SettingsClick, true)
            findNavController().navigate(R.id.action_gpsSettingsFragment_to_registerReminderFragment,bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        if(Utils.checkLocationPermission(requireContext())){
            val bundle=Bundle()
            bundle.putBoolean(Constants.GpsSettings,true)
            findNavController().navigate(R.id.action_gpsSettingsFragment_to_registerReminderFragment,bundle)
            sessionManager.saveBooleanData(SessionManager.SettingsClick, true)
        }
    }

    override fun observer() {

    }

}