package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

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
            binding.cancelBtn.visibility=View.GONE
        }

        if (NewCreateAccountRequestModel.isRucEligible){

        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
    }

}