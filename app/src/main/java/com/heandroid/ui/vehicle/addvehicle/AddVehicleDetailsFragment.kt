package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.*
import com.heandroid.databinding.FragmentAddVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentAddVehicleDetailsBinding
    private lateinit var mVehicleDetails: VehicleResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAddVehicleDetailsBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        dataBinding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"
        dataBinding.subTitle.text = "Country of registration ${mVehicleDetails.plateInfo.country}"
    }

    private fun initCtrl() {
        dataBinding.nextBtn.setOnClickListener {
            if (dataBinding.makeInputEditText.text!!.isNotEmpty()
                && dataBinding.modelInputEditText.text!!.isNotEmpty()
                && dataBinding.colorInputEditText.text!!.isNotEmpty()
            ) {
                mVehicleDetails.vehicleInfo.color = dataBinding.colorInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.make = dataBinding.makeInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.model = dataBinding.modelInputEditText.text.toString()

                val bundle = Bundle().apply {
                    putSerializable(Constants.DATA, mVehicleDetails)
                }
                findNavController().navigate(R.id.addVehicleClassesFragment, bundle)
            }
        }

    }

}