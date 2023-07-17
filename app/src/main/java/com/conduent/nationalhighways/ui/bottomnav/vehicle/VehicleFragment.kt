package com.conduent.nationalhighways.ui.bottomnav.vehicle

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtActivity
import com.conduent.nationalhighways.ui.vehicle.vehiclegroup.VehicleGroupMgmtActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
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
            if (it == Constants.BUSINESS_ACCOUNT||it==Constants.EXEMPT_ACCOUNT) {
                isBusinessAccount = true
            }
        }
        if (isBusinessAccount) {
            binding.vehicleManagementLyt.visible()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
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