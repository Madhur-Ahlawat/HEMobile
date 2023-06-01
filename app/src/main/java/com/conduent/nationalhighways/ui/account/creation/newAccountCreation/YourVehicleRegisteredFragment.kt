package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentYourVehicleRegisteredBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class YourVehicleRegisteredFragment : BaseFragment<FragmentYourVehicleRegisteredBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentYourVehicleRegisteredBinding= FragmentYourVehicleRegisteredBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}