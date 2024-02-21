package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentYourVehicleRegisteredBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class YourVehicleRegisteredFragment : BaseFragment<FragmentYourVehicleRegisteredBinding>(),
    View.OnClickListener {
    private var data: CrossingDetailsModelsResponse? = null
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentYourVehicleRegisteredBinding =
        FragmentYourVehicleRegisteredBinding.inflate(inflater, container, false)

    override fun init() {
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)
        binding.btnVehicleContinue.setOnClickListener(this)
        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            binding.btnVehicleContinue.isEnabled =
                R.id.radioButtonYes == checkedId || R.id.radioButtonNo == checkedId
        }
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        if (data == null) {
            data = CrossingDetailsModelsResponse()
        }
//        data?.vehicleClass = nonUKVehicleModel?.vehicleClass
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnVehicleContinue -> {
                val id: Int = binding.radioGroupYesNo.checkedRadioButtonId
                if (id == R.id.radioButtonNo) {
                    NewCreateAccountRequestModel.plateCountry = Constants.COUNTRY_TYPE_NON_UK
                    data?.plateCountry = Constants.COUNTRY_TYPE_NON_UK
                    data?.veicleUKnonUK = false
                } else {
                    NewCreateAccountRequestModel.plateCountry = Constants.COUNTRY_TYPE_UK
                    data?.plateCountry = Constants.COUNTRY_TYPE_UK
                    data?.veicleUKnonUK = true
                }
                if(data?.vehicleClass?.isEmpty() == true){
                    data?.vehicleClass = nonUKVehicleModel?.vehicleClass
                }
                if(data?.plateNo?.isEmpty() == true){
                    data?.plateNo = nonUKVehicleModel?.plateNumber ?: ""
                }
                val bundle = Bundle()
                bundle.putString(Constants.data_type, "dvla")
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.VEHICLE_DETAIL, nonUKVehicleModel)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                if (navFlowCall.equals(Constants.TRANSFER_CROSSINGS)) {
                    findNavController().navigate(
                        R.id.action_yourVehicleFragment_to_addVehicleFragment,
                        bundle
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_yourVehicleFragment_to_addVehicleFragment, bundle
                    )
                }
            }
        }
    }

}