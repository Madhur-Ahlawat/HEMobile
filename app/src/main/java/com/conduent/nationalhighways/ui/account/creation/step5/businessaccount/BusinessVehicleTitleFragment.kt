package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleTitleFragmentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
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
//        findNavController().navigate(R.id.action_businessVehicleTitleFragment_to_businessVehicleUKFragment, bundle)
    }
}