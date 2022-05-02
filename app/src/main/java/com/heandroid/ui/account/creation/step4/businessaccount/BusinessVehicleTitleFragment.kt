package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessVehicleTitleFragmentBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.VehicleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleTitleFragment: BaseFragment<FragmentBusinessVehicleTitleFragmentBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) =  FragmentBusinessVehicleTitleFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
    }

    override fun initCtrl() {
        binding.continueButton.setOnClickListener(this@BusinessVehicleTitleFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?){

        val bundle = Bundle()
        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
        findNavController().navigate(R.id.action_businessVehicleTitleFragment_to_businessVehicleUKFragment, bundle)
    }
}