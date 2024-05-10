package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentManualAddressBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.viewModel.LrdsEligibilityViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_FROM_POST_CODE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.UK_COUNTRY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.splCharAddress1
import com.conduent.nationalhighways.utils.common.Utils.splCharAddress2
import com.conduent.nationalhighways.utils.common.Utils.splCharPostCode
import com.conduent.nationalhighways.utils.common.Utils.splCharTownCity
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.NHAutoCompleteTextview
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern


@AndroidEntryPoint
class ManualAddressFragment() : BaseFragment<FragmentManualAddressBinding>(),
    View.OnClickListener, NHAutoCompleteTextview.AutoCompleteSelectedTextListener, Parcelable {

    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var totalCountriesList: ArrayList<CountriesModel> = ArrayList()
    private var loader: LoaderDialog? = null
    private var requiredAddress: Boolean = false
    private var requiredAddress2: Boolean = true
    private var requiredCityTown: Boolean = false
    private var requiredPostcode: Boolean = false
    private var requiredCountry: Boolean = false
    private var country: String = ""
    private var isViewCreated: Boolean = false
    private val viewModelProfile: ProfileViewModel by viewModels()
    private val lrdsViewModel: LrdsEligibilityViewModel by viewModels()
    private var oldPostCode: String = ""
    private var editPostCode: String = ""

    constructor(parcel: Parcel) : this() {
        requiredAddress = parcel.readByte() != 0.toByte()
        requiredAddress2 = parcel.readByte() != 0.toByte()
        requiredCityTown = parcel.readByte() != 0.toByte()
        requiredPostcode = parcel.readByte() != 0.toByte()
        requiredCountry = parcel.readByte() != 0.toByte()
        country = parcel.readString() ?: ""
        isViewCreated = parcel.readByte() != 0.toByte()
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualAddressBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.POST_CODE) == true) {
            oldPostCode = arguments?.getString(Constants.POST_CODE) ?: ""
        }
        if (arguments?.containsKey(Constants.EDIT_POST_CODE) == true) {
            editPostCode = arguments?.getString(Constants.EDIT_POST_CODE) ?: ""
        }
        if (NewCreateAccountRequestModel.zipCode.isNotEmpty()) {
            binding.postCode.setText(NewCreateAccountRequestModel.zipCode)
            requiredPostcode = true

        }

        binding.address.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.address2.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.townCity.editText.addTextChangedListener(GenericTextWatcher(2))
        binding.postCode.editText.addTextChangedListener(GenericTextWatcher(3))


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
        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {

                binding.address.setText(NewCreateAccountRequestModel.addressLine1)
                binding.address2.setText(NewCreateAccountRequestModel.addressLine2)
                binding.townCity.setText(NewCreateAccountRequestModel.townCity)
                binding.country.setSelectedValue(NewCreateAccountRequestModel.country)
                requiredCountry = true

                binding.postCode.setText(NewCreateAccountRequestModel.zipCode)

                checkButton()
                if (NewCreateAccountRequestModel.personalAccount) {
                    setPersonalView()
                }
            }

            PROFILE_MANAGEMENT -> {
                if(requireActivity() is HomeActivityMain){
                    HomeActivityMain.setTitle(resources.getString(R.string.profile_address))
                }
                (navData as ProfileDetailModel).personalInformation?.let {
                    if (oldPostCode.equals(editPostCode)) {
                        binding.address.editText.setText(it.addressLine1)
                        binding.address2.editText.setText(it.addressLine2)
                        binding.townCity.editText.setText(it.city)
                        binding.country.setSelectedValue(it.country ?: "")
                        requiredAddress = true
                        requiredAddress2 = true
                        requiredCountry = true
                        binding.postCode.editText.setText(it.zipcode)
                    } else {
                        binding.postCode.editText.setText(editPostCode)
                    }
                }

                requiredPostcode = true
                checkButton()
                if ((navData as ProfileDetailModel).accountInformation?.accountType.equals(
                        Constants.PERSONAL_ACCOUNT,
                        true
                    )
                ) {
                    setPersonalView()
                }

            }

            else -> {
                if (NewCreateAccountRequestModel.personalAccount) {
                    setPersonalView()
                }
            }

        }
        if(requireActivity() is HomeActivityMain){
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        }
        if(requireActivity() is CreateAccountActivity){
            (requireActivity() as CreateAccountActivity).focusToolBarCreateAccount()
        }
    }

    private fun setPersonalView() {
        binding.txtHeading.text = getString(R.string.personal_address)
    }


    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.btnFindAddress.setOnClickListener(this)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getCountries()
    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.countriesList, ::getCountriesList)
            observe(lrdsViewModel.lrdsEligibilityCheck, ::handleLrdsApiResponse)
        }

        isViewCreated = true
        observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)

    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()
                bundle.putString(
                    Constants.NAV_FLOW_KEY,
                    Constants.PROFILE_MANAGEMENT_ADDRESS_CHANGED
                )
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_manualaddressfragment_to_resetForgotPassword,
                    bundle
                )
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorModel?.message)
                }
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnFindAddress -> {

                NewCreateAccountRequestModel.addressLine1 = binding.address.getText().toString()
                NewCreateAccountRequestModel.addressLine2 = binding.address2.getText().toString()
                NewCreateAccountRequestModel.townCity = binding.townCity.getText().toString()
                NewCreateAccountRequestModel.country =
                    binding.country.selectedItemDescription.toString()

                NewCreateAccountRequestModel.address_country_code =
                    getCountryCode(binding.country.selectedItemDescription.toString())
                NewCreateAccountRequestModel.zipCode = binding.postCode.getText().toString()
                if (navFlowCall.equals(PROFILE_MANAGEMENT, true)) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                    val data = navData as ProfileDetailModel?

                    updateProfileDetails(data)

                } else {
                    if (NewCreateAccountRequestModel.personalAccount) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        hitlrdsCheckApi()
                    } else {
                        redirectToNextPage()
                    }
                }


            }
        }
    }

    private fun getCountryCode(country: String): String {
        val filteredModels = totalCountriesList.filter { it.countryName == country }
        if (filteredModels.size > 0) {
            return filteredModels.get(0).countryCode ?: ""
        }
        return country
    }

    private fun getCountryCodeName(country: String): String {
        val filteredModels = totalCountriesList.filter { it.countryCode == country }
        if (filteredModels.size > 0) {
            return filteredModels.get(0).countryName ?: ""
        }
        return country
    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                totalCountriesList.clear()
                totalCountriesList = response.data as ArrayList<CountriesModel>
                response.data.forEach {
                    it.countryName?.let { it1 -> countriesList.add(it1) }
                }
                countriesList.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it }
                )


                if (countriesList.contains(UK_COUNTRY)) {
                    countriesList.remove(UK_COUNTRY)
                    countriesList.add(0, UK_COUNTRY)
                }

                binding.apply {
                    country.dataSet.clear()
                    country.dataSet.addAll(countriesList)
                }
                if (navFlowCall.equals(PROFILE_MANAGEMENT)) {
                    binding.country.setSelectedValue(
                        getCountryCodeName(
                            (navData as ProfileDetailModel).personalInformation?.country
                                ?: UK_COUNTRY
                        )
                    )
                } else if (navFlowCall.equals(EDIT_ACCOUNT_TYPE) or navFlowCall.equals(EDIT_SUMMARY)) {
                    binding.country.setSelectedValue(
                        NewCreateAccountRequestModel.country
                    )
                } else {
                    binding.country.setSelectedValue(UK_COUNTRY)
                }
                requiredCountry = true

                binding.country.clearFocus()
                binding.country.setDropDownItemSelectListener(this)
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(response.errorModel)) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }

            else -> {
            }

        }
    }

    private fun hitlrdsCheckApi() {
        val lrdsEligibilityCheck = LrdsEligibiltyRequest()
        if (NewCreateAccountRequestModel.country.equals(UK_COUNTRY, true)) {
            lrdsEligibilityCheck.country = "UK"
        } else {
            lrdsEligibilityCheck.country = NewCreateAccountRequestModel.country
        }
        lrdsEligibilityCheck.addressline1 = NewCreateAccountRequestModel.addressLine1
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
                    addressErrorMessage(charSequence)
                }

                1 -> {
                    address2ErrorMessage(charSequence)
                }

                2 -> {
                    townCityErrorMessage(charSequence)
                }

                3 -> {
                    postCodeErrorMessage(charSequence)
                }
            }


        }

        override fun afterTextChanged(editable: Editable?) {

        }


    }


    private fun addressErrorMessage(char: CharSequence?) {
        if (binding.address.getText().toString().trim().isEmpty()) {
            binding.address.removeError()
            requiredAddress = false
        } else {
            if (binding.address.getText().toString().trim().length <= 200) {
                requiredAddress = if (Utils.hasSpecialCharacters(
                        binding.address.getText().toString().trim().replace(" ", ""),
                        splCharAddress1
                    )
                ) {
                    binding.address.setErrorText(getString(R.string.str_building_number_character_allowed))
                    false
                } else {
                    binding.address.removeError()
                    true
                }
            } else {
                requiredAddress = false
                binding.address.setErrorText(getString(R.string.str_building_number_error_message))
            }
        }
        checkButton()

    }

    private fun address2ErrorMessage(char: CharSequence?) {
        if (binding.address2.getText().toString().isEmpty()) {
            binding.address2.removeError()
            requiredAddress2 = true
        }
        requiredAddress2 = if (binding.address2.getText().toString().trim().length <= 100) {
            if (Utils.hasSpecialCharacters(
                    binding.address2.getText().toString().trim().replace(" ", ""),
                    splCharAddress2
                )
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

    private fun postCodeErrorMessage(char: CharSequence?) {
        requiredPostcode = if (binding.postCode.getText().toString().trim().isEmpty()) {
            binding.postCode.removeError()
            false
        } else {
            val string = binding.postCode.getText().toString().trim()
            val regex: Pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!]")
            val finalString = string.replace(" ", "")
            val pattern = Pattern.compile("[^-]*-")
            val matcher: Matcher = pattern.matcher(finalString)
            var count = 0
            while (matcher.find()) {
                count++
            }

            if (!(Utils.hasLowerCase(
                    binding.postCode.editText.text.toString().trim()
                ) || Utils.hasUpperCase(
                    binding.postCode.editText.text.toString().trim()
                )) || !hasDigits(
                    binding.postCode.editText.text.toString().trim()
                ) || Utils.hasSpecialCharacters(
                    binding.postCode.getText().toString().trim(),
                    splCharPostCode
                )
            ) {
                binding.postCode.setErrorText(getString(R.string.postcode_must_not_contain_special_characters_except_hyphen))
                false
            } else if (finalString.length < 4 || finalString.length > 10) {
                binding.postCode.setErrorText(getString(R.string.postcode_must_be_between_4_and_10_characters))
                false
            } else {
                binding.postCode.removeError()
                true
            }
        }

        checkButton()

    }

    private fun townCityErrorMessage(char: CharSequence?) {
        if (binding.townCity.getText().toString().trim().isEmpty()) {
            binding.townCity.removeError()
            requiredCityTown = false
        } else {
            requiredCityTown = if (binding.townCity.getText().toString().trim().length <= 50) {
                if (hasDigits(binding.townCity.getText().toString().trim())) {
                    binding.townCity.setErrorText(getString(R.string.str_town_city_character_allowed))
                    false
                } else if (Utils.hasSpecialCharacters(
                        binding.townCity.getText().toString().trim().replace(" ", ""),
                        splCharTownCity
                    )
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


    private fun handleLrdsApiResponse(response: Resource<LrdsEligibilityResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (response) {
            is Resource.Success -> {
                NewCreateAccountRequestModel.isManualAddress = true
                if (response.data?.lrdsEligible.equals("true", true)) {

                    findNavController().navigate(
                        R.id.action_manualaddressfragment_to_createAccountEligibleLRDS2,
                        bundle()
                    )

                } else {
                    redirectToNextPage()
                }


            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(response.errorModel)
                ) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }

            else -> {

            }
        }

    }

    private fun redirectToNextPage() {
        when (navFlowCall) {

            EDIT_SUMMARY -> {
                if (navFlowFrom.equals(EDIT_FROM_POST_CODE)) {
                    findNavController().navigate(
                        R.id.action_manualaddressfragment_to_createAccountSummary,
                        bundle()
                    )
                } else {
                    findNavController().popBackStack()
                }

            }

            else -> {
                if (NewCreateAccountRequestModel.personalAccount) {
                    findNavController().navigate(
                        R.id.action_manualaddressfragment_to_createAccountTypesFragment,
                        bundle()
                    )

                } else {
                    findNavController().navigate(
                        R.id.action_manualaddressfragment_to_forgotPasswordFragment,
                        bundle()
                    )

                }
            }

        }

    }

    private fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        return bundle
    }

    private fun updateProfileDetails(data: ProfileDetailModel?) {

        val request = Utils.returnEditProfileModel(
            data?.accountInformation?.businessName ?: "",
            data?.accountInformation?.fein,
            data?.personalInformation?.firstName,
            data?.personalInformation?.lastName,
            NewCreateAccountRequestModel.addressLine1,
            NewCreateAccountRequestModel.addressLine2,
            NewCreateAccountRequestModel.townCity,
            "HE",
            NewCreateAccountRequestModel.zipCode,
            data?.personalInformation?.zipCodePlus,
            NewCreateAccountRequestModel.address_country_code,
            data?.personalInformation?.emailAddress,
            data?.personalInformation?.primaryEmailStatus,
            data?.personalInformation?.pemailUniqueCode,
            data?.personalInformation?.phoneCell,
            data?.personalInformation?.phoneCellCountryCode,
            data?.personalInformation?.phoneDay,
            data?.personalInformation?.phoneDayCountryCode,
            data?.personalInformation?.fax,
            data?.accountInformation?.smsOption,
            data?.personalInformation?.eveningPhone,
            data?.accountInformation?.stmtDelivaryMethod,
            data?.accountInformation?.stmtDelivaryInterval,
            Utils.returnMfaStatus(data?.accountInformation?.mfaEnabled ?: ""),
            accountType = data?.accountInformation?.accountType

        )
        viewModelProfile.updateUserDetails(request)

    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (requiredAddress) 1 else 0)
        parcel.writeByte(if (requiredAddress2) 1 else 0)
        parcel.writeByte(if (requiredCityTown) 1 else 0)
        parcel.writeByte(if (requiredPostcode) 1 else 0)
        parcel.writeByte(if (requiredCountry) 1 else 0)
        parcel.writeString(country)
        parcel.writeByte(if (isViewCreated) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ManualAddressFragment> {
        override fun createFromParcel(parcel: Parcel): ManualAddressFragment {
            return ManualAddressFragment(parcel)
        }

        override fun newArray(size: Int): Array<ManualAddressFragment?> {
            return arrayOfNulls(size)
        }
    }

    override fun onAutoCompleteItemClick(item: String, selected: Boolean) {
        if (item.isEmpty()) {
            binding.labelCountryCode.invisible()
        } else {
            binding.labelCountryCode.visible()
        }
        if (selected) {
            requiredCountry = true
            country = item
        } else {
            if (countriesList.size > 0) {
                requiredCountry = countriesList.any { it == item }
                country = item
            } else {
                requiredCountry = false
                country = ""
            }
        }

        checkButton()

    }

}