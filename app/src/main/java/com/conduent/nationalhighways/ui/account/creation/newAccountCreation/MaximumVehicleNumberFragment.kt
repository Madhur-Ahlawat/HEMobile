package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentMaximumVehicleNumberBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment


class MaximumVehicleNumberFragment : BaseFragment<FragmentMaximumVehicleNumberBinding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaximumVehicleNumberBinding =
        FragmentMaximumVehicleNumberBinding.inflate(inflater, container, false)

    override fun init() {
        if (NewCreateAccountRequestModel.isExempted){
            binding.textMaximumVehicle.text=getString(R.string.str_vehicle_exempt_detail_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_exempt_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.cancelBtn.visibility=View.GONE
            binding.btnContinue.text = getString(R.string.str_continue)
        }

        if (NewCreateAccountRequestModel.isRucEligible){
            binding.textMaximumVehicle.text=getString(R.string.str_no_ruc_desc)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_exempt_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.cancelBtn.visibility=View.VISIBLE
            binding.btnContinue.text = getString(R.string.str_add_to_account)
        }

        if (NewCreateAccountRequestModel.isVehicleAlreadyAdded){
            binding.textMaximumVehicle.text=getString(R.string.str_vehicle_already_exist_desc,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_already_added_system,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.VISIBLE
            binding.cancelBtn.visibility=View.VISIBLE
            binding.btnContinue.text = getString(R.string.str_add_another)
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
    }

}