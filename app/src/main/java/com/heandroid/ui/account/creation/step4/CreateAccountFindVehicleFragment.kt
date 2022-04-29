package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentCreateAccountFindVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(), View.OnClickListener {

    private var isAccountVehicle = false
    private var requestModel: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 4, 5)

        //This need to be removed
        requestModel = CreateAccountRequestModel(
            referenceId = 0, securityCd = 0, accountType = "", tcAccepted = "Y", firstName = "",
            lastName = "", address1 = "", city = "", stateType = "", countryType = "",
            zipCode1 = "", emailAddress = "", cellPhone = "", eveningPhone = "", smsOption = "Y",
            password = "", digitPin = "", companyName = "", fein = "", nonRevenueOption = "",
            ftvehicleList = null, creditCardType = "", creditCardNumber = "", maskedNumber = "", creditCExpMonth = "",
            creditCExpYear = "", securityCode = "", cardFirstName = "", cardMiddleName = "", cardLastName = "",
            billingAddressLine1 = "", billingAddressLine2 = "", cardCity = "", cardStateType = "", cardZipCode = "",
            thresholdAmount = null, replenishmentAmount = null, transactionAmount = null, planType = null, enable = false,
            classType = "", vehicleNo = "")
        requestModel?.accountType = BUSINESS_ACCOUNT
    }

    override fun initCtrl() {
        // This has to be add later
       // requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)

        binding.apply {
            addVrmInput.onTextChanged {
                binding.isEnable = addVrmInput.length() > 1
            }
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 4, 5)
            continueBtn.setOnClickListener(this@CreateAccountFindVehicleFragment)
        }
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continue_btn -> {

                var country = "UK"
                if (binding.addVrmInput.text.toString().isNotEmpty()) {
                    country = if (!binding.switchView.isChecked) {
                        "Non-UK"
                    } else {
                        "UK"
                    }
                    requestModel?.countryType = country

                    when (requestModel?.accountType) {
                        BUSINESS_ACCOUNT -> businessAccountVehicle(country)
                        else -> standardAccountVehicle(country)
                    }
                } else {
                    requireContext().showToast("Please enter your vehicle number")
                }
            }
        }
    }

    private fun businessAccountVehicle(country: String) {
          requestModel?.countryType = country
          requestModel?.vehicleNo = binding.addVrmInput.text.toString()

           val bundle = Bundle()
           bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)

           if(country == "UK")
                findNavController().navigate(R.id.action_findVehicleFragment_to_businessVehicleUKListFragment, bundle)
             else
               findNavController().navigate(R.id.action_findVehicleFragment_to_businessVehicleNonUKMakeFragment, bundle)
    }

    private fun standardAccountVehicle(country: String) {
        if (country == "UK") {
            isAccountVehicle = true
            val bundle = Bundle()
            bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
            bundle.putBoolean("IsAccountVehicle", isAccountVehicle)
            bundle.putString("VehicleNo", binding.addVrmInput.text.toString())
            findNavController().navigate(
                R.id.action_findYourVehicleFragment_to_createAccountVehicleListFragment,
                bundle
            )
        } else {
            val nonVehicleModel = NonUKVehicleModel()
            nonVehicleModel.plateCountry = "Non-UK"
            nonVehicleModel.vehiclePlate = binding.addVrmInput.text.toString()
            nonVehicleModel.isFromCreateNonVehicleAccount = true
            val bundle = Bundle()
            bundle.putParcelable(
                Constants.CREATE_ACCOUNT_DATA,
                arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
            )

            bundle.putParcelable(Constants.CREATE_ACCOUNT_NON_UK, nonVehicleModel)
            findNavController().navigate(
                R.id.action_findYourVehicleFragment_to_callNonUkVehicleAddFragment,
                bundle
            )
        }
    }
}