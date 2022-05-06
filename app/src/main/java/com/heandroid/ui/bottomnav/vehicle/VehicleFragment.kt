package com.heandroid.ui.bottomnav.vehicle

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtActivity
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleFragment : BaseFragment<FragmentVehicleBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentVehicleBinding = FragmentVehicleBinding.inflate(inflater, container, false)

    override fun init() { }

    override fun observer() { }

    override fun initCtrl() {
        binding.vehicleListLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_LIST)
        }

        binding.addVehicleLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_ADD)
        }
        binding.vehicleHistoryLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_HISTORY)

        }
        binding.vehicleCrossingHistoryLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_CROSSING_HISTORY)
        }
    }

    private fun startVehicleMgmtActivity(type: Int) {
        startActivity(Intent(requireContext(), VehicleMgmtActivity::class.java).apply {
            putExtra(Constants.VEHICLE_SCREEN_KEY, type)
        })
    }

}