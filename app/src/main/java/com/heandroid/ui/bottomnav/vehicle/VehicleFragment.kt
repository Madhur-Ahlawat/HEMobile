package com.heandroid.ui.bottomnav.vehicle

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtActivity
import com.heandroid.ui.vehicle.vehiclegroup.VehicleGroupMgmtActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VehicleFragment : BaseFragment<FragmentVehicleBinding>() {

    private var isBusinessAccount = false
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleBinding = FragmentVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.fetchAccountType()?.let {
            if (it == Constants.BUSINESS_ACCOUNT){
                isBusinessAccount = true
            }
        }
        if (isBusinessAccount) {
            binding.apply {
                addVehicleLyt.gone()
                vehicleHistoryLyt.gone()
            }
        } else {
            binding.vehicleManagementLyt.gone()
        }
    }

    override fun observer() {}

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
        binding.vehicleManagementLyt.setOnClickListener {
            startVehicleGroupMgmtActivity()
        }
    }

    private fun startVehicleMgmtActivity(type: Int) {
        startActivity(Intent(requireContext(), VehicleMgmtActivity::class.java).apply {
            putExtra(Constants.VEHICLE_SCREEN_KEY, type)
        })
    }

    private fun startVehicleGroupMgmtActivity() {
        startActivity(Intent(requireContext(), VehicleGroupMgmtActivity::class.java))
    }

}