package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentGroupVehicleDetailBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleGroupVehicleDetailsFragment :
    BaseFragment<FragmentGroupVehicleDetailBinding>() {

    private var mVehicleDetails: VehicleResponse? = null
    private var vehicleGroup: VehicleGroupResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGroupVehicleDetailBinding.inflate(inflater, container, false)

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        vehicleGroup = arguments?.getParcelable(Constants.VEHICLE_GROUP)
        setDataToView()
    }

    override fun initCtrl() {
        binding.apply {
            editDetailsBtn.setOnClickListener {
                mVehicleDetails?.let {
                    val bundle = Bundle().apply {
                        putParcelable(Constants.DATA, it)
                        putParcelable(Constants.VEHICLE_GROUP, vehicleGroup)
                    }
                    findNavController().navigate(
                        R.id.action_vehicleGroupVehicleDetailsFragment_to_vehicleGroupVehicleEditDetailsFragment,
                        bundle
                    )
                }
            }
            crossingHistoryLayout.setOnClickListener {
                findNavController().navigate(R.id.action_vehicleGroupVehicleDetailsFragment_to_vehicleGroupCrossingHistoryFragment)
            }
        }
    }

    override fun observer() {}


    private fun setDataToView() {
        mVehicleDetails?.let { response ->
            binding.vehicleData = response
            binding.tvAddedDate.text =
                DateUtils.convertDateFormat(response.vehicleInfo?.effectiveStartDate, 1)
            if (response.plateInfo?.vehicleGroup?.isEmpty() == true) {
                binding.groupLayout.gone()
            }
        }
    }

}
