package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.databinding.FragmentManualAddressBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.viewModel.LrdsEligibilityViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.UK_COUNTRY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualAddressFragment : BaseFragment<FragmentManualAddressBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var loader: LoaderDialog? = null
    private var requiredAddress: Boolean = false
    private var requiredAddress2: Boolean = true
    private var requiredCityTown: Boolean = false
    private var requiredPostcode: Boolean = false
    private var requiredCountry: Boolean = false
    private var country: String = ""
    private var isViewCreated: Boolean = false

    private val lrdsViewModel: LrdsEligibilityViewModel by viewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualAddressBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if (NewCreateAccountRequestModel.personalAccount) {
            binding.txtHeading.text = getString(R.string.personal_address)
        }

        if (NewCreateAccountRequestModel.zipCode.isNotEmpty()) {
            binding.postCode.setText(NewCreateAccountRequestModel.zipCode)
            requiredPostcode  = true

        }

        binding.address.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.address2.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.townCity.editText.addTextChangedListener(GenericTextWatcher(2))
        binding.postCode.editText.addTextChangedListener(GenericTextWatcher(3))

        binding.country.dropDownItemSelectListener = this

        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i])
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.postCode.editText.filters = arrayOf(filter)
        binding.postCode.setMaxLength(10)
        if(NewCreateAccountRequestModel.isEditCall) {
            binding.address.setText(NewCreateAccountRequestModel.addressline1)
            binding.address2.setText(NewCreateAccountRequestModel.addressline2)
            binding.townCity.setText(NewCreateAccountRequestModel.townCity)
            binding.postCode.setText(NewCreateAccountRequestModel.zipCode)
            binding.country.setSelectedValue(NewCreateAccountRequestModel.country)
            requiredCountry = true
            checkButton()
        }
    }


    override fun initCtrl() {
        binding.btnFindAddress.setOnClickListener(this)
        viewModel.getCountries()
    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.countriesList, ::getCountriesList)
            observe(lrdsViewModel.lrdsEligibilityCheck, ::handleLrdsApiResponse)
        }

        isViewCreated = true


    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnFindAddress -> {

                NewCreateAccountRequestModel.addressline1 = binding.address.getText().toString()
                NewCreateAccountRequestModel.addressline2 = binding.address2.getText().toString()
                NewCreateAccountRequestModel.townCity = binding.townCity.getText().toString()
                NewCreateAccountRequestModel.country = binding.country.selectedItemDescription.toString()
                NewCreateAccountRequestModel.zipCode = binding.postCode.getText().toString()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                hitlrdsCheckApi()


            }
        }
    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                countriesList.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it }
                )


                if (countriesList.contains(UK_COUNTRY)) {
                    countriesList.remove(UK_COUNTRY)
                    countriesList.add(0, UK_COUNTRY)
                }

                binding.apply {
                    country.dataSet.addAll(countriesList)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }

    private fun hitlrdsCheckApi() {
        val lrdsEligibilityCheck = LrdsEligibiltyRequest()
        if(NewCreateAccountRequestModel.country.equals(UK_COUNTRY,true)){
            lrdsEligibilityCheck.country = "UK"
        }else {
            lrdsEligibilityCheck.country = NewCreateAccountRequestModel.country
        }
        lrdsEligibilityCheck.addressline1 = NewCreateAccountRequestModel.addressline1
        lrdsEligibilityCheck.firstName = NewCreateAccountRequestModel.firstName
        lrdsEligibilityCheck.lastName = NewCreateAccountRequestModel.lastName
        lrdsEligibilityCheck.zipcode1 = NewCreateAccountRequestModel.zipCode
        lrdsEligibilityCheck.action = Constants.LRDS_ELIGIBILITY_CHECK


        lrdsViewModel.getLrdsEligibilityResponse(lrdsEligibilityCheck)
    }


    inner class GenericTextWatcher(val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            when (index) {
                0 -> {
                    addressErrorMessage()
                }

                1 -> {
                    address2ErrorMessage()
                }

                2 -> {
                    townCityErrorMessage()
                }

                3 -> {
                    postCodeErrorMessage()
                }
            }


        }

        override fun afterTextChanged(editable: Editable?) {

        }


    }


    private fun addressErrorMessage() {
        if (binding.address.getText().toString().trim().isEmpty()) {
            binding.address.setErrorText(getString(R.string.str_building_error_message))
            requiredAddress = false
        } else {
            if (binding.address.getText().toString().trim().length < 200) {
                requiredAddress = if (binding.address.getText().toString()
                        .contains(Utils.addressSpecialCharacter)
                ) {
                    binding.address.setErrorText(getString(R.string.str_building_number_character_allowed))
                    false
                } else {
                    binding.address.removeError()
                    true
                }

            } else {
                requiredAddress = if (binding.address.getText().toString().trim().length > 200) {
                    binding.address.setErrorText(getString(R.string.str_building_number_error_message))
                    false
                } else {
                    binding.address.removeError()
                    true
                }
            }
        }
        checkButton()

    }

    private fun address2ErrorMessage() {
        if (binding.address2.getText().toString().isEmpty()) {
            binding.address2.removeError()
            requiredAddress2 = true
        }
        requiredAddress2 = if (binding.address2.getText().toString().trim().length < 100) {
            if (binding.address2.getText().toString().trim()
                    .contains(Utils.addressSpecialCharacter)
            ) {
                binding.address2.setErrorText(getString(R.string.str_address_line2_character_allowed))
                false

            } else {
                binding.address2.removeError()
                true
            }
        } else {
            binding.address2.setErrorText(getString(R.string.str_address_line2_length_error_message))
            false
        }

        checkButton()

    }

    private fun postCodeErrorMessage() {
        requiredPostcode = if (binding.postCode.getText().toString().trim().isEmpty()) {
            binding.postCode.setErrorText(getString(R.string.str_post_code_error_message))
            false
        } else {
            val string = binding.postCode.getText().toString().trim()
            val finalString = string.replace(" ", "")
            if (finalString.length < 4 || finalString.length > 11) {
                binding.postCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                false

            } else {
                binding.postCode.removeError()
                true
            }
        }

        checkButton()

    }

    private fun townCityErrorMessage() {
        if (binding.townCity.getText().toString().trim().isEmpty()) {
            binding.townCity.setErrorText(getString(R.string.str_town_city_error_message))
            requiredCityTown = false
        } else {
            requiredCityTown = if (binding.townCity.getText().toString().trim().length < 50) {

                if (binding.townCity.getText().toString().trim()
                        .contains(Utils.addressSpecialCharacter)
                ) {
                    binding.townCity.setErrorText(getString(R.string.str_town_city_character_allowed))
                    false
                } else {
                    binding.townCity.removeError()
                    true
                }
            } else {
                binding.townCity.setErrorText(getString(R.string.str_town_city_length_error_message))
                false
            }
        }

        checkButton()
    }

    private fun checkButton() {
        if (requiredAddress && requiredAddress2 && requiredCityTown && requiredPostcode && requiredCountry) {
            binding.btnFindAddress.enable()
        } else {
            binding.btnFindAddress.disable()
        }
    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {

    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        requiredCountry = true
        country = selectedItem
        checkButton()

    }

    private fun handleLrdsApiResponse(response: Resource<LrdsEligibilityResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (response) {
            is Resource.Success -> {
                NewCreateAccountRequestModel.isManualAddress = true
                if (response.data?.lrdsEligible.equals("true", true)) {

                    findNavController().navigate(R.id.action_manualaddressfragment_to_createAccountEligibleLRDS2)

                } else {
                    if(NewCreateAccountRequestModel.isEditCall &&  NewCreateAccountRequestModel.isAccountTypeEditCall.not()) {
                        findNavController().popBackStack()
                    }else{
                        if (NewCreateAccountRequestModel.personalAccount) {
                            findNavController().navigate(R.id.action_manualaddressfragment_to_createAccountTypesFragment)

                        } else {
                            val bundle = Bundle()
                            bundle.putString(
                                Constants.NAV_FLOW_KEY,
                                Constants.ACCOUNT_CREATION_EMAIL_FLOW
                            )
                            findNavController().navigate(
                                R.id.action_manualaddressfragment_to_forgotPasswordFragment,
                                bundle
                            )

                        }
                    }

                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {

            }
        }

    }

}