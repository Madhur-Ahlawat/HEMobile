package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentAddVehicleDetailsBinding>() {

    private lateinit var mVehicleDetails: VehicleResponse

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {

    }

    override fun init() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        binding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"
        binding.subTitle.text = "Country of registration ${mVehicleDetails.plateInfo.country}"
    }

    override fun initCtrl() {
        binding.nextBtn.setOnClickListener {
            if (binding.makeInputEditText.text!!.isNotEmpty()
                && binding.modelInputEditText.text!!.isNotEmpty()
                && binding.colorInputEditText.text!!.isNotEmpty()
            ) {
                mVehicleDetails.vehicleInfo.color = binding.colorInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.make = binding.makeInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.model = binding.modelInputEditText.text.toString()

                val bundle = Bundle().apply {
                    putSerializable(Constants.DATA, mVehicleDetails)
                }
                findNavController().navigate(R.id.addVehicleClassesFragment, bundle)
            }
        }

    }

}