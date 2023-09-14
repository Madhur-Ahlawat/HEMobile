package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentMobileNumberCaptureVcBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_MOBILE_FLOW
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HWMobileNumberCaptureVC : BaseFragment<FragmentMobileNumberCaptureVcBinding>(),
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {

    private var requiredCountryCode = false
    private var requiredMobileNumber = false
    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesCodeList: MutableList<String> = ArrayList()
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var isItMobileNumber = true
    private val viewModelProfile: ProfileViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var countriesModel: List<CountriesModel?>? = ArrayList()
    private var fullCountryNameWithCode: MutableList<String> = ArrayList()
    private var oldMobileNumber = ""
    private var oldMobileCountryCode = ""
    private var oldTelephoneNumber = ""
    private var oldTelephoneCountryCode = ""


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMobileNumberCaptureVcBinding.inflate(inflater, container, false)

    override fun init() {

        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER

        binding.inputCountry.dropDownItemSelectListener = this
        Log.e(
            "TAG",
            "init: *communicationTextMessage* " + NewCreateAccountRequestModel.communicationTextMessage + " *twoStepVerification* " + NewCreateAccountRequestModel.twoStepVerification
        )
        if (!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) {
            setTelephoneView()
        } else {
            setMobileView()
        }

        binding.btnNext.setOnClickListener(this)
        when (navFlowCall) {
            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                if (!isViewCreated) {
                    oldMobileNumber = NewCreateAccountRequestModel.mobileNumber ?: ""
                    oldMobileCountryCode = NewCreateAccountRequestModel.countryCode ?: ""
                    oldTelephoneNumber = NewCreateAccountRequestModel.telephoneNumber ?: ""
                    oldTelephoneCountryCode =
                        NewCreateAccountRequestModel.telephone_countryCode ?: ""
                }
                if (isItMobileNumber) {
                    NewCreateAccountRequestModel.mobileNumber?.let {
                        binding.inputMobileNumber.setText(
                            it
                        )
                    }
                    NewCreateAccountRequestModel.countryCode?.let {
                        binding.inputCountry.setSelectedValue(
                            it
                        )
                    }
                } else {
                    NewCreateAccountRequestModel.telephoneNumber?.let {
                        binding.inputMobileNumber.setText(
                            it
                        )
                    }
                    NewCreateAccountRequestModel.telephone_countryCode?.let {
                        binding.inputCountry.setSelectedValue(
                            it
                        )
                    }
                }

                requiredCountryCode = binding.inputCountry.getText()?.isNotEmpty() == true
                checkButton()
            }

            PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                title?.text = getString(R.string.communication_preferences)
                setMobileView()
            }

            Constants.PROFILE_MANAGEMENT_2FA_CHANGE, PROFILE_MANAGEMENT, PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                val data = navData as ProfileDetailModel?

                val title: TextView? = requireActivity().findViewById(R.id.title_txt)
                if (data?.personalInformation?.phoneCell.isNullOrEmpty()){
                    title?.text = getString(R.string.profile_telephone_number)

                    setTelephoneView()
                }else{
                    title?.text = getString(R.string.profile_mobile_number)
                    setMobileView()

                }
                setData()
            }
        }

        isViewCreated = true

    }

    private fun setData() {
        val data = navData as ProfileDetailModel?
        if (data != null) {
            if (data.personalInformation?.phoneCell.isNullOrEmpty().not()) {
                setMobileView()
                data.personalInformation?.phoneCell?.let { binding.inputMobileNumber.setText(it) }
            } else if (data.personalInformation?.phoneDay.isNullOrEmpty().not()) {
                setTelephoneView()
                data.personalInformation?.phoneDay?.let { binding.inputMobileNumber.setText(it) }
            }
            data.personalInformation?.phoneCellCountryCode?.let {
                binding.inputCountry.setSelectedValue(
                    it
                )
            }
            requiredCountryCode = true
            checkButton()
        }
    }


    private fun setTelephoneView() {
        val data = navData as ProfileDetailModel?

        isItMobileNumber = false
        binding.txtTitleTop.text = getString(R.string.str_what_is_your_number)
        binding.inputMobileNumber.setLabel(getString(R.string.str_telephone_number_optional))
        binding.txtBottom.visibility = View.GONE
        requiredMobileNumber = true
        if (NewCreateAccountRequestModel.prePay||data?.accountInformation?.accSubType.equals(Constants.PAYG).not()) {
            binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
        } else {
            binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(0))
        }
    }

    private fun setMobileView() {
        isItMobileNumber = true
        binding.inputMobileNumber.setLabel(getString(R.string.str_mobile_number))
        binding.txtTitleTop.text = getString(R.string.str_what_mobile_number)
        binding.txtBottom.visibility = View.VISIBLE

        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun initCtrl() {
    }

    override fun observer() {
        if (!isViewCreated) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

            viewModel.getCountries()


            observe(viewModel.countriesList, ::getCountriesList)

            observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)
            observe(viewModel.countriesCodeList, ::getCountryCodesList)
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
        }

    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()

        }
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()

                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                bundle.putBoolean(Constants.IS_MOBILE_NUMBER, isItMobileNumber)
                findNavController().navigate(
                    R.id.action_HWMobileNumberCaptureVC_to_resetFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesCodeList.clear()
                for (i in 0..(countriesModel?.size?.minus(1) ?: 0)) {
                    for (j in 0..(response.data?.size?.minus(1) ?: 0)) {
                        if (countriesModel?.get(i)?.id == response.data?.get(j)?.id) {
                            fullCountryNameWithCode.add(
                                countriesModel?.get(i)?.countryName + " " + "(" + response.data?.get(
                                    j
                                )?.key + ")"
                            )
                        }
                    }

                }
                Log.d("fullCountryWithCode", Gson().toJson(fullCountryNameWithCode))
                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                fullCountryNameWithCode.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })


                if (fullCountryNameWithCode.contains(Constants.UNITED_KINGDOM)) {
                    fullCountryNameWithCode.remove(Constants.UNITED_KINGDOM)
                    fullCountryNameWithCode.add(0, Constants.UNITED_KINGDOM)
                }

                binding.apply {
                    inputCountry.dataSet.clear()
                    inputCountry.dataSet.addAll(fullCountryNameWithCode)
                    inputCountry.setSelectedValue(Constants.UNITED_KINGDOM)
                    requiredCountryCode = binding.inputCountry.getText()?.isNotEmpty() == true
                    if (!NewCreateAccountRequestModel.prePay) {
                        checkButton()
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

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                val mobileNumber = binding.inputMobileNumber.getText().toString().trim()
                val countryCode = binding.inputCountry.selectedItemDescription.toString()
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

                val noChanges: Boolean = if(isItMobileNumber){
                    countryCode == oldMobileCountryCode && mobileNumber == oldMobileNumber
                }else{
                    countryCode == oldTelephoneCountryCode && mobileNumber == oldTelephoneNumber
                }
                when (navFlowCall) {

                    EDIT_SUMMARY -> {


                        if (noChanges) {
                            findNavController().navigate(
                                R.id.action_HWMobileNumberCaptureVC_to_accountSummaryFragment,
                                bundle
                            )
                        } else {
                            val res: Int =
                                R.id.action_HWMobileNumberCaptureVC_to_accountSummaryFragment
                            handleNavFlow(mobileNumber, countryCode, bundle, res)
                        }
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        if (noChanges) {
                            findNavController().navigate(
                                R.id.action_AccountChangeType_HWMobileNumberCaptureVC_to_vehicleListFragment,
                                bundle
                            )
                        }else{
                            assignNumbers(mobileNumber,countryCode)

                            if (isItMobileNumber){
                                hitApi()
                            }else{
                                findNavController().navigate(
                                    R.id.action_AccountChangeType_HWMobileNumberCaptureVC_to_vehicleListFragment,
                                    bundle
                                )
                            }


                        }
                    }

                    PROFILE_MANAGEMENT_MOBILE_CHANGE, PROFILE_MANAGEMENT, Constants.PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        val data = navData as ProfileDetailModel?
                        if (data != null) {
                            if (isItMobileNumber) {
                                val phone = data.personalInformation?.phoneCell
                                if (phone.isNullOrEmpty().not() && phone.equals(
                                        binding.inputMobileNumber.getText().toString().trim(), true
                                    )
                                ) {
                                    findNavController().popBackStack()
                                } else {
                                    hitApi()
                                }
                            } else {
                                val landline = data.personalInformation?.phoneDay
                                if (landline.isNullOrEmpty().not() && landline.equals(
                                        binding.inputMobileNumber.getText().toString().trim(), true
                                    )
                                ) {
                                    findNavController().popBackStack()
                                } else {
                                    if (data.accountInformation?.accountType.equals(
                                            Constants.PERSONAL_ACCOUNT,
                                            true
                                        )
                                    ) {
                                        updateStandardUserProfile(data)
                                    } else {
                                        updateBusinessUserProfile(data)
                                    }
                                }
                            }

                        }
                    }

                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        hitApi()
                    }

                    else -> {
                        val res: Int = R.id.action_HWMobileNumberCaptureVC_to_createVehicleFragment
                        handleNavFlow(mobileNumber, countryCode, bundle, res)
                    }
                }
            }
        }
    }

    private fun assignNumbers(mobileNumber: String, countryCode: String) {
        if (isItMobileNumber) {
            NewCreateAccountRequestModel.mobileNumber = mobileNumber
            NewCreateAccountRequestModel.countryCode = countryCode
        } else {
            NewCreateAccountRequestModel.telephoneNumber = mobileNumber
            NewCreateAccountRequestModel.telephone_countryCode = countryCode
        }
    }

    private fun handleNavFlow(mobileNumber: String, countryCode: String, bundle: Bundle, res: Int) {
        assignNumbers(mobileNumber, countryCode)
        if (!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) {
            findNavController().navigate(res, bundle)
        } else {
            hitApi()
        }
    }


    override fun onRetryClick() {

    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        /* if (loader?.isVisible == true) {
             loader?.dismiss()
         }*/
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                countriesModel = response.data
                viewModel.getCountryCodesList()

                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                countriesList.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it }
                )


                if (countriesList.contains(Constants.UK_COUNTRY)) {
                    countriesList.remove(Constants.UK_COUNTRY)
                    countriesList.add(0, Constants.UK_COUNTRY)
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }


    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?, start: Int, before: Int, count: Int
        ) {
            requiredCountryCode = binding.inputCountry.getText()?.isNotEmpty() == true

            if (charSequence.toString().isEmpty()) {
                requiredMobileNumber = index == 0
            } else {
                val phoneNumber = binding.inputMobileNumber.getText().toString().trim()
                if (isItMobileNumber && (binding.inputCountry.getSelectedDescription()
                        .equals("UK +44", true) || binding.inputCountry.getSelectedDescription()
                        .equals(Constants.UNITED_KINGDOM, true))
                ) {
                    requiredMobileNumber = if (phoneNumber.isNotEmpty()) {
                        if (phoneNumber.matches(Utils.UK_MOBILE_REGEX)) {
                            binding.inputMobileNumber.removeError()
                            true
                        } else {
                            binding.inputMobileNumber.setErrorText(getString(R.string.str_uk_phoneNumber_error_message))
                            false
                        }
                    } else {
                        false
                    }
                } else {

                    requiredMobileNumber = if (phoneNumber.isNotEmpty()) {
                        if (phoneNumber.matches(Utils.PHONENUMBER)) {
                            binding.inputMobileNumber.removeError()
                            true
                        } else {
                            if (isItMobileNumber) {
                                binding.inputMobileNumber.setErrorText(getString(R.string.str_non_uk_phoneNumber_error_message))
                            } else {
                                binding.inputMobileNumber.setErrorText(getString(R.string.telephone_error_message))
                            }
                            false
                        }
                    } else {
                        false
                    }
                }

            }
            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }


    private fun checkButton() {
        if (requiredCountryCode && requiredMobileNumber) {
            binding.btnNext.enable()
        } else {
            binding.btnNext.disable()
        }
    }

    private fun hitApi() {

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            Constants.SMS, binding.inputMobileNumber.getText().toString().trim()
        )
        createAccountViewModel.emailVerificationApi(request)


    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()
                bundle.putParcelable(
                    "data", RequestOTPModel(
                        Constants.SMS, binding.inputMobileNumber.getText().toString().trim()
                    )
                )
                bundle.putString(Constants.PHONE_COUNTRY_CODE,
                    binding.inputCountry.selectedItemDescription?.let { getRequiredText(it) })

                bundle.putParcelable(
                    "response", SecurityCodeResponseModel(
                        resource.data?.emailStatusCode, 0L, resource.data?.referenceId, true
                    )
                )

                when (navFlowCall) {


                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arguments?.getParcelable(
                                Constants.NAV_DATA_KEY,
                                CommunicationPrefsRequestModel::class.java
                            )
                        } else {
                            arguments?.getParcelable(Constants.NAV_DATA_KEY)
                        }
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }

                    PROFILE_MANAGEMENT_MOBILE_CHANGE , Constants.PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        val data = navData as ProfileDetailModel?
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }

                    else -> {
                        bundle.putString(Constants.NAV_FLOW_KEY, ACCOUNT_CREATION_MOBILE_FLOW)
                        bundle.putString(Constants.Edit_REQUEST_KEY, navFlowCall)
                    }
                }

                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                findNavController().navigate(
                    R.id.action_HWMobileNumberCaptureVC_to_forgotOtpFragment,
                    bundle
                )
            }

            is Resource.DataError -> {

                ErrorUtil.showError(binding.root, resource.errorMsg)

            }

            else -> {
            }
        }
    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")", "")

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        binding.inputMobileNumber.setText("")
        binding.inputMobileNumber.removeError()
    }

    private fun updateBusinessUserProfile(
        dataModel: ProfileDetailModel?
    ) {
        dataModel?.run {
            val request = UpdateProfileRequest(
                firstName = personalInformation?.firstName,
                lastName = personalInformation?.lastName,
                addressLine1 = personalInformation?.addressLine1,
                addressLine2 = personalInformation?.addressLine2,
                city = personalInformation?.city,
                state = personalInformation?.state,
                zipCode = personalInformation?.zipcode,
                zipCodePlus = personalInformation?.zipCodePlus,
                country = personalInformation?.country,
                emailAddress = personalInformation?.emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = personalInformation?.pemailUniqueCode,
                phoneCell = personalInformation?.phoneCell,
                phoneDay = binding.inputMobileNumber.getText().toString().trim(),
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            viewModelProfile.updateUserDetails(request)
        }


    }

    private fun updateStandardUserProfile(
        dataModel: ProfileDetailModel?
    ) {

        dataModel?.personalInformation?.run {
            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                zipCode = zipcode,
                zipCodePlus = zipCodePlus,
                country = country,
                emailAddress = emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = pemailUniqueCode,
                phoneCell = phoneCell,
                phoneDay = binding.inputMobileNumber.getText().toString().trim(),
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = ""
            )

            viewModelProfile.updateUserDetails(request)
        }

    }

}