package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentMaximumVehicleNumberBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment


class MaximumVehicleNumberFragment : BaseFragment<FragmentMaximumVehicleNumberBinding>() {




    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaximumVehicleNumberBinding= FragmentMaximumVehicleNumberBinding.inflate(inflater,container,false)

    override fun init() {
        val accountData = NewCreateAccountRequestModel
        if(accountData.isVehicleAlreadyAdded){
            binding.maximumVehicleAdded.text = "Vehicle AJ66 MHA has already been assigned to this account"
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}