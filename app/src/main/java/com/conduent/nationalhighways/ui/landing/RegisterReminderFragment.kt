package com.conduent.nationalhighways.ui.landing

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRegisterReminderBinding
import com.conduent.nationalhighways.receiver.GeofenceBroadcastReceiver
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.setToolBarTitle
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.GeofenceUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices


class RegisterReminderFragment : BaseFragment<FragmentRegisterReminderBinding>() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterReminderBinding =
        FragmentRegisterReminderBinding.inflate(inflater, container, false)

    override fun init() {
        showToolBar(true)
        setToolBarTitle(resources.getString(R.string.str_register_to_receive_notifications))
        GeofenceUtils.startGeofence(this.requireContext())
    }


    override fun initCtrl() {
        if (checkLocationPermission()) {
            binding.switchCommunication.isChecked = true
        } else {
            binding.switchCommunication.isChecked = true
        }

        binding.switchCommunication.setOnClickListener {
            if (binding.switchCommunication.isChecked) {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun observer() {

    }


    private fun checkLocationPermission(): Boolean {

        val fineLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }


}