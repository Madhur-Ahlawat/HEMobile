package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleNonUkMakeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.onTextChanged

class BusinessVehicleNonUKMakeFragment: BaseFragment<FragmentBusinessVehicleNonUkMakeBinding>(),
    View.OnClickListener {

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentBusinessVehicleNonUkMakeBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        nonUKVehicleModel = NonUKVehicleModel()

        binding.vehicleRegNum.text = getString(R.string.vehicle_reg_num, requestModel?.vehicleNo)
        binding.countryRegistration.text = getString(R.string.country_reg, requestModel?.plateCountryType)    }

    override fun initCtrl() {

        binding.makeInputEditText.onTextChanged {
            checkButton()
        }
        binding.modelInputEditText.onTextChanged {
            checkButton()
        }
        binding.colorInputEditText.onTextChanged {
            checkButton()
        }

        binding.nextBtnBusiness.setOnClickListener(this@BusinessVehicleNonUKMakeFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.nextBtnBusiness -> {
                if (binding.makeInputEditText.getText().toString().trim().isNotEmpty()
                    && binding.modelInputEditText.getText().toString().trim().isNotEmpty()
                    && binding.colorInputEditText.getText().toString().trim().isNotEmpty()) {

                    nonUKVehicleModel?.apply {
                        vehicleMake = binding.makeInputEditText.getText().toString()
                        vehicleModel = binding.modelInputEditText.getText().toString()
                        vehicleColor = binding.colorInputEditText.getText().toString()
                    }

                    val bundle = Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                    bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                    findNavController().navigate(R.id.action_businessNonUkMakeFragment_to_businessNonUKClassFragment, bundle)
                }
            }
        }
    }

    private fun checkButton() {
        binding.model = (binding.makeInputEditText.getText().toString().trim().isNotEmpty()
                && binding.modelInputEditText.getText().toString().trim().isNotEmpty()
                && binding.colorInputEditText.getText().toString().trim().isNotEmpty())
    }

}
