package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentYourVehicleRegisteredBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class YourVehicleRegisteredFragment : BaseFragment<FragmentYourVehicleRegisteredBinding>(),
    View.OnClickListener {

    private var nonUKVehicleModel: NewVehicleInfoDetails? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentYourVehicleRegisteredBinding= FragmentYourVehicleRegisteredBinding.inflate(inflater,container,false)

    override fun init() {
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)
        binding.btnVehicleContinue.setOnClickListener(this)
        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            binding.btnVehicleContinue.isEnabled = R.id.radioButtonYes == checkedId || R.id.radioButtonNo == checkedId
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnVehicleContinue->{
                val id: Int = binding.radioGroupYesNo.checkedRadioButtonId
                NewCreateAccountRequestModel.plateCountry=Constants.COUNTRY_TYPE_UK
                if (id == R.id.radioButtonNo) {
                    NewCreateAccountRequestModel.plateCountry =Constants.COUNTRY_TYPE_NON_UK
                }
                val bundle = Bundle()
//                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.VEHICLE_DETAIL, nonUKVehicleModel)
                findNavController().navigate(
                    R.id.action_yourVehicleFragment_to_addVehicleFragment,bundle
                )
            }
        }
    }

}