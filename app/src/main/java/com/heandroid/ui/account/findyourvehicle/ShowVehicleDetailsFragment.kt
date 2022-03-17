package com.heandroid.ui.account.creation.findyourvehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.notification.AlertMessage
import com.heandroid.databinding.FragmentShowVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import org.json.JSONObject

class ShowVehicleDetailsFragment : BaseFragment<FragmentShowVehicleDetailsBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentShowVehicleDetailsBinding {
       return FragmentShowVehicleDetailsBinding.inflate(inflater, container, false)
    }

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
            confirmBtnVehicle.setOnClickListener(this@ShowVehicleDetailsFragment)
            notVehicle.setOnClickListener(this@ShowVehicleDetailsFragment)
        }
    }

    override fun observer() {

    }

    override fun onClick(v: View?) {
        v.let {
            when(v?.id){
                R.id.confirm_btn_vehicle ->{
                    // Navigate to step 5
                }
                R.id.not_vehicle -> {
                    findNavController().navigate(R.id.action_showVehicleDetailsFragment_to_findYourVehicleFragment)
                }
                else -> {
                }
            }
        }
    }
}