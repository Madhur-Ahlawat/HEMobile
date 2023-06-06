package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentRemoveVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class RemoveVehicleFragment : BaseFragment<FragmentRemoveVehicleBinding>() {



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRemoveVehicleBinding= FragmentRemoveVehicleBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}