package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.databinding.FragmentCreateAccountVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountVehicleDetailsFragment : BaseFragment<FragmentCreateAccountVehicleDetailsBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountVehicleDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        val vehicleInfoDetails = arguments?.getParcelable(Constants.FIND_VEHICLE_DATA) as VehicleInfoDetails?
        val plateInfo = vehicleInfoDetails?.retrievePlateInfoDetails
        binding.apply {
            vehicleNumber.text = plateInfo?.plateNumber
            classType.text = plateInfo?.vehicleClass
            vehicleMake.text = plateInfo?.vehicleMake
            vehicleModel.text = plateInfo?.vehicleModel
            vehicleColor.text = plateInfo?.vehicleColor
        }
    }

    override fun initCtrl() {
         binding.apply {
            confirmBtnVehicle.setOnClickListener(this@CreateAccountVehicleDetailsFragment)
            notVehicle.setOnClickListener(this@CreateAccountVehicleDetailsFragment)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(DATA,arguments?.getParcelable(DATA))
        when(v?.id){
            R.id.confirm_btn_vehicle ->{ findNavController().navigate(R.id.action_showVehicleDetailsFragment_to_accountVehiclePaymentFragment,bundle) }
            R.id.not_vehicle -> { findNavController().navigate(R.id.action_showVehicleDetailsFragment_to_findYourVehicleFragment,bundle) }
        }
    }
}