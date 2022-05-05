package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.databinding.FragmentBusinessVehicleNonUkMakeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.onTextChanged

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
        binding.countryRegistration.text = getString(R.string.country_reg, requestModel?.countryType)    }

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
                if (binding.makeInputEditText.text.toString().trim().isNotEmpty()
                    && binding.modelInputEditText.text.toString().trim().isNotEmpty()
                    && binding.colorInputEditText.text.toString().trim().isNotEmpty()) {

                    nonUKVehicleModel?.apply {
                        vehicleMake = binding.makeInputEditText.text.toString()
                        vehicleModel = binding.modelInputEditText.text.toString()
                        vehicleColor = binding.colorInputEditText.text.toString()
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
        binding.model = (binding.makeInputEditText.text.toString().trim().isNotEmpty()
                && binding.modelInputEditText.text.toString().trim().isNotEmpty()
                && binding.colorInputEditText.text.toString().trim().isNotEmpty())
    }

}
