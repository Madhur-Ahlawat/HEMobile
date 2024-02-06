package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.app.PendingIntent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentMobileNumberCaptureVcBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_MOBILE_FLOW
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_MOBILE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.UNITED_KINGDOM
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.getCountryCodeRequiredText
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.NHAutoCompleteTextview
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.crashlytics.internal.Logger.TAG
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber

@AndroidEntryPoint
class HWMobileNumberCaptureVC : BaseFragment<FragmentMobileNumberCaptureVcBinding>(),
    View.OnClickListener, OnRetryClickListener,
    NHAutoCompleteTextview.AutoCompleteSelectedTextListener {
    val dashboardViewmodel: DashboardViewModel by activityViewModels()
    var personalInformationModel: PersonalInformation? = null
    var accountInformationModel: AccountInformation? = null
    private val countryCodesList: MutableList<String> = mutableListOf()
    private var retrievedPhoneNumber: String? = null
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
    private var title: TextView? = null
    private var data: ProfileDetailModel? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMobileNumberCaptureVcBinding.inflate(inflater, container, false)

    override fun init() {
//        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
//        registerReceiver(requireContext(),smsVerificationReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
        title = requireActivity().findViewById(R.id.title_txt)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

        }
        viewModel.getCountries()
        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER

        if (!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) {
            setTelephoneView()
        } else {
            setMobileView()
        }

        binding.btnNext.setOnClickListener(this)
        when (navFlowCall) {
            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY, EDIT_MOBILE -> {
                binding.incompatiblePoint3Tv.text =
                    resources.getString(R.string.str_may_change_mobile_number)
                if (!isViewCreated) {
                    oldMobileNumber = NewCreateAccountRequestModel.mobileNumber ?: ""
                    oldMobileCountryCode = NewCreateAccountRequestModel.countryCode ?: ""
                    oldTelephoneNumber = NewCreateAccountRequestModel.telephoneNumber ?: ""
                    oldTelephoneCountryCode =
                        NewCreateAccountRequestModel.telephone_countryCode ?: ""
                }
                if (isItMobileNumber) {
                    NewCreateAccountRequestModel.mobileNumber?.let {
                        binding.inputMobileNumber.editText.setText(
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
                        binding.inputMobileNumber.editText.setText(
                            it
                        )
                    }
                    NewCreateAccountRequestModel.telephone_countryCode?.let {
                        binding.inputCountry.setSelectedValue(
                            it
                        )
                    }
                }
                checkIncompatibleCountry(binding.inputCountry.selectedItemDescription.toString(), 1)

                requiredCountryCode = binding.inputCountry.text?.isNotEmpty() == true
                checkButton()
            }

            PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                binding.incompatiblePoint3Tv.text =
                    resources.getString(R.string.str_may_change_mobile_number_two_step)
                personalInformationModel = dashboardViewmodel.personalInformationData.value
                accountInformationModel = dashboardViewmodel.accountInformationData.value
                if (requireActivity() !is CreateAccountActivity) {
                    title?.text = getString(R.string.communication_preferences)
                }
                setMobileView()
                setMobileData()
            }

            PROFILE_MANAGEMENT_2FA_CHANGE -> {
                binding.incompatiblePoint3Tv.text =
                    resources.getString(R.string.str_may_change_mobile_number_two_step)
                personalInformationModel = dashboardViewmodel.personalInformationData.value
                accountInformationModel = dashboardViewmodel.accountInformationData.value
                if (requireActivity() !is CreateAccountActivity) {
                    title?.text = getString(R.string.str_profile_two_factor_verification)
                }
                setMobileData()

            }

            PROFILE_MANAGEMENT, PROFILE_MANAGEMENT_MOBILE_CHANGE -> {
                binding.incompatiblePoint3Tv.text =
                    resources.getString(R.string.str_may_change_mobile_number_two_step)

                personalInformationModel = dashboardViewmodel.personalInformationData.value
                accountInformationModel = dashboardViewmodel.accountInformationData.value
                data = navData as ProfileDetailModel?

                if (data?.personalInformation?.phoneCell.isNullOrEmpty()) {
                    setTelephoneView()
                } else {
                    setMobileView()
                }
                setData()
            }

            else -> {
                binding.inputMobileNumber.editText.setText(
                    NewCreateAccountRequestModel.mobileNumber ?: ""
                )
            }
        }

        isViewCreated = true
        requestHint()
    }

    private fun requestHint() {
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
        val phoneNumberHintIntentResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult())
            { result
                ->
                try {
                    var matchedCountry: String? = null
                    var matchedCountryCode: String? = null
                    retrievedPhoneNumber =
                        Identity.getSignInClient(requireActivity()).getPhoneNumberFromIntent(
                            result.data
                        ).replace(" ", "").replace("-", "")
                    var phoneNumberUtil = PhoneNumberUtil.createInstance(requireContext())
                    val phoneNumberParsed: PhoneNumber =
                        phoneNumberUtil.parse(retrievedPhoneNumber, "")
                    var phoneNumberString = phoneNumberUtil.format(
                        phoneNumberParsed,
                        PhoneNumberUtil.PhoneNumberFormat.E164
                    )
                    val phoneNumberParsedFinal: PhoneNumber =
                        phoneNumberUtil.parse(phoneNumberString, "")
                    val mCountryCode = phoneNumberParsed.countryCode
                        .toString()
                    val phoneNumberStringFinal = phoneNumberParsedFinal.nationalNumber
                    countryCodesList.forEachIndexed { index, s ->
                        if (mCountryCode.contains
                                (
                                s.replace
                                    (
                                    "+",
                                    ""
                                )
                            )
                        ) {
                            fullCountryNameWithCode.forEachIndexed { index, s2 ->
                                var cc = "(" + s + ")"
                                if (s2.contains
                                        (cc)
                                ) {
                                    matchedCountry = s2
                                    matchedCountryCode = "+" + mCountryCode
                                }
                            }
                        }
                    }
                    Log.e("TAG", "requestHint: matchedCountry $matchedCountry")
                    if (matchedCountry.isNullOrEmpty()) {
                        binding.inputCountry.setSelectedValue(UNITED_KINGDOM)
                        binding.inputMobileNumber.setText(phoneNumberStringFinal.toString())
                        binding.inputMobileNumber.setErrorText(
                            getString(
                                R.string.unfortunately_at_this_time_we_do_not_support_your_mobile_number
                            )
                        )
                    } else {
                        binding.inputCountry.setSelectedValue(
                            matchedCountry ?: UNITED_KINGDOM
                        )
                        binding.inputMobileNumber.setText(phoneNumberStringFinal.toString())
                    }

                    if (binding.inputCountry.selectedItemDescription == UNITED_KINGDOM) {
                        requiredCountryCode = true
                    }
                    checkIncompatibleCountry(binding.inputCountry.selectedItemDescription, 2)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Phone Number Hint failed"
                    )
                }
                if (NewCreateAccountRequestModel.prePay) {
                    binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
                } else if (data != null && data?.accountInformation?.accSubType.equals
                        (Constants.PAYG)
                        .not()
                ) {
                    binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
                } else {
                    binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(0))
                }
            }
        Identity.getSignInClient(requireActivity())
            .getPhoneNumberHintIntent(request).addOnSuccessListener { result: PendingIntent
                ->
                try {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(result).build()
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Launching the PendingIntent failed"
                    )
                }
            }.addOnFailureListener {
                Log.e(
                    TAG,
                    "Phone Number Hint failed"
                )
            }
    }

    private fun setData() {
        data = navData as ProfileDetailModel?
        if (data != null) {
            if (data?.personalInformation?.phoneCell.isNullOrEmpty().not()) {
                setMobileView()
                data?.personalInformation?.phoneCell?.let {
                    binding.inputMobileNumber.editText.setText(
                        it
                    )
                }
            } else if (data?.personalInformation?.phoneDay.isNullOrEmpty().not()) {
                setTelephoneView()
                data?.personalInformation?.phoneDay?.let {
                    binding.inputMobileNumber.editText.setText(
                        it
                    )
                }
            }
            binding.inputCountry.setSelectedValue(
                data?.personalInformation?.phoneCellCountryCode ?: UNITED_KINGDOM
            )

            checkIncompatibleCountry(
                binding.inputCountry.selectedItemDescription.toString(),
                3,
                false
            )
            requiredCountryCode = true
            checkButton()
        }
    }

    private fun setMobileData() {
        data = navData as ProfileDetailModel?
        if (data != null) {
            if (data?.personalInformation?.phoneCell.isNullOrEmpty().not()) {
                setMobileView()
                data?.personalInformation?.phoneCell?.let {
                    binding.inputMobileNumber.editText.setText(
                        it
                    )
                }
            }

            binding.inputCountry.setSelectedValue(
                data?.personalInformation?.phoneCellCountryCode ?: UNITED_KINGDOM
            )

            checkIncompatibleCountry(
                binding.inputCountry.selectedItemDescription.toString(),
                3,
                false
            )
            requiredCountryCode = true
            checkButton()
        }
    }


    private fun setTelephoneView() {
        isItMobileNumber = false
        requiredMobileNumber = true
        data = navData as ProfileDetailModel?
        if (requireActivity() !is CreateAccountActivity && requireActivity() !is MakeOffPaymentActivity) {
            title?.text = getString(R.string.profile_phone_number)
        }
        binding.inputMobileNumber.setLabel(getString(R.string.phone_number))
        binding.txtTitleTop.text = getString(R.string.str_what_is_your_number)
        binding.txtBottom.visibility = View.GONE
        if (NewCreateAccountRequestModel.prePay) {
            binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
        } else if (data != null && data?.accountInformation?.accSubType.equals(Constants.PAYG)
                .not()
        ) {
            binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))

        } else {
            binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(0))
        }
    }

    private fun setMobileView() {
        isItMobileNumber = true
        if (requireActivity() !is CreateAccountActivity && navFlowCall != PROFILE_MANAGEMENT_COMMUNICATION_CHANGED) {
            title?.text = getString(R.string.profile_mobile_number)
        }
        binding.inputMobileNumber.setLabel(getString(R.string.mobile_number))
        binding.txtTitleTop.text = getString(R.string.str_what_mobile_number)
        binding.txtBottom.visibility = View.VISIBLE
        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(viewModel.countriesList, ::getCountriesList)
        observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)
        observe(viewModel.countriesCodeList, ::getCountryCodesList)
        observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                data = navData as ProfileDetailModel?
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
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
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
                fullCountryNameWithCode.clear()
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


                if (fullCountryNameWithCode.contains(UNITED_KINGDOM)) {
                    fullCountryNameWithCode.remove(UNITED_KINGDOM)
                    fullCountryNameWithCode.add(0, UNITED_KINGDOM)
                }

                binding.apply {
                    inputCountry.dataSet.clear()
                    inputCountry.dataSet.addAll(fullCountryNameWithCode)
                    separateCountryCodeListFromNameList(fullCountryNameWithCode)
                }


                if (navFlowCall == PROFILE_MANAGEMENT_MOBILE_CHANGE || navFlowCall == PROFILE_MANAGEMENT_COMMUNICATION_CHANGED || navFlowCall == PROFILE_MANAGEMENT_2FA_CHANGE) {
                    var userCountryCode = data?.personalInformation?.phoneDayCountryCode

                    if (data?.personalInformation?.phoneCell.isNullOrEmpty().not()) {
                        userCountryCode = data?.personalInformation?.phoneCellCountryCode
                    }
                    if (userCountryCode.isNullOrEmpty() == true) {
                        binding.inputCountry.setSelectedValue(UNITED_KINGDOM)
                    } else {
                        fullCountryNameWithCode.forEachIndexed { _, fullCountryName ->
                            val countryCode = getCountryCode(fullCountryName)
                            if (countryCode == userCountryCode) {
                                binding.inputCountry.setSelectedValue(fullCountryName)
                                return@forEachIndexed
                            }
                        }
                    }
                } else {
                    if (NewCreateAccountRequestModel.countryCode?.isEmpty() == true) {
                        binding.inputCountry.setSelectedValue(UNITED_KINGDOM)
                    } else {
                        binding.inputCountry.setSelectedValue(
                            NewCreateAccountRequestModel.countryCode ?: ""
                        )
                    }
                }

                if (navFlowCall != PROFILE_MANAGEMENT_MOBILE_CHANGE) {
                    checkIncompatibleCountry(
                        binding.inputCountry.selectedItemDescription.toString(),
                        4
                    )
                }


                requiredCountryCode =
                    fullCountryNameWithCode.any { it == binding.inputCountry.selectedItemDescription }

                if (binding.inputCountry.selectedItemDescription == UNITED_KINGDOM) {
                    requiredCountryCode = true
                }


                if (!NewCreateAccountRequestModel.prePay) {
                    checkButton()
                }

                if (binding.inputMobileNumber.editText.text?.isNotEmpty() == true) {
                    checkButton()
                }

                binding.inputCountry.clearFocus()
                binding.inputCountry.setDropDownItemSelectListener(this)


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

    private fun separateCountryCodeListFromNameList(fullCountryNameWithCode: MutableList<String>) {
        countryCodesList.clear()
        fullCountryNameWithCode.forEachIndexed { _, s ->
            countryCodesList.add(s.substring(s.indexOf("(") + 1, s.indexOf(")")))
        }
    }

    private fun getCountryCode(selectedItem: String): String {
        val openingParenIndex = selectedItem.indexOf("(")
        val closingParenIndex = selectedItem.indexOf(")")

        val extractedText =
            if (openingParenIndex != -1 && closingParenIndex != -1 && closingParenIndex > openingParenIndex) {
                selectedItem.substring(openingParenIndex + 1, closingParenIndex)
            } else {
                ""
            }
        return extractedText
    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")", "")


    override fun onClick(v: View?) {
        hideKeyboard()

        when (v?.id) {
            binding.btnNext.id -> {
                val mobileNumber = binding.inputMobileNumber.getText().toString().trim()
                val countryCode = binding.inputCountry.selectedItemDescription
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)

                val noChanges: Boolean = if (isItMobileNumber) {
                    countryCode == oldMobileCountryCode && mobileNumber == oldMobileNumber
                } else {
                    countryCode == oldTelephoneCountryCode && mobileNumber == oldTelephoneNumber
                }

                if (NewCreateAccountRequestModel.isCountryNotSupportForSms == false) {
                    NewCreateAccountRequestModel.notSupportedCountrySaveDetails = true
                } else {
                    if (binding.payasugoCb.visibility == View.VISIBLE) {
                        NewCreateAccountRequestModel.notSupportedCountrySaveDetails =
                            binding.payasugoCb.isChecked
                    } else {
                        NewCreateAccountRequestModel.notSupportedCountrySaveDetails = true
                    }
                }

                var payasugoAccount = false
                var payasugoChecked = false

                if (binding.payasugoCb.visibility == View.VISIBLE && binding.incompatibleLl.visibility== View.VISIBLE) {
                    payasugoAccount=true
                    payasugoChecked = binding.payasugoCb.isChecked
                }

                when (navFlowCall) {
                    EDIT_MOBILE -> {
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

                    EDIT_SUMMARY -> {
                        val res: Int =
                            R.id.action_HWMobileNumberCaptureVC_to_accountSummaryFragment
                        handleNavFlow(mobileNumber, countryCode, bundle, res)
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        if (noChanges) {
                            findNavController().navigate(
                                R.id.action_AccountChangeType_HWMobileNumberCaptureVC_to_vehicleListFragment,
                                bundle
                            )
                        } else {
                            assignNumbers(mobileNumber, countryCode)

                            if (isItMobileNumber && NewCreateAccountRequestModel.isCountryNotSupportForSms == false) {
                                hitApi()
                            } else {
                                findNavController().navigate(
                                    R.id.action_AccountChangeType_HWMobileNumberCaptureVC_to_vehicleListFragment,
                                    bundle
                                )
                            }
                        }
                    }

                    PROFILE_MANAGEMENT_MOBILE_CHANGE, PROFILE_MANAGEMENT, PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        if (payasugoAccount) {
                            if (payasugoChecked) {
                               profileNumberUpdate()
                            } else {
                                findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_profileManagementFragment)
                            }
                        }else{
                            profileNumberUpdate()
                        }

                    }

                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        if (payasugoAccount) {
                            if (payasugoChecked) {
                                updateCommunicaionPref()
                            } else {
                                findNavController().navigate(R.id.action_HWMobileNumberCaptureVC_to_accountFragment)
                            }
                        }else{
                            updateCommunicaionPref()
                        }
                    }

                    else -> {
                        val res: Int = R.id.action_HWMobileNumberCaptureVC_to_createVehicleFragment
                        handleNavFlow(mobileNumber, countryCode, bundle, res)
                    }
                }
            }
        }
    }

    private fun updateCommunicaionPref() {

        if (NewCreateAccountRequestModel.isCountryNotSupportForSms == true) {
            loader?.show(
                requireActivity().supportFragmentManager,
                Constants.LOADER_DIALOG
            )

            updateProfileDetails(
                personalInformationModel,
                accountInformationModel,
                "N",
                "N"
            )

        } else {
            hitApi()
        }
    }

    private fun profileNumberUpdate() {
        Log.e(TAG, "profileNumberUpdate:isItMobileNumber "+isItMobileNumber )
        data = navData as ProfileDetailModel?
        if (data != null) {
            if (isItMobileNumber) {
                val phone = data?.personalInformation?.phoneCell
                if ((!navFlowCall.equals(PROFILE_MANAGEMENT_2FA_CHANGE)) && phone.isNullOrEmpty()
                        .not() && phone.equals(
                        binding.inputMobileNumber.getText().toString()
                            .trim(), true
                    )
                ) {
                    Log.e(TAG, "profileNumberUpdate:isItMobileNumber " )
                    findNavController().popBackStack()
                } else {
                    if (NewCreateAccountRequestModel.isCountryNotSupportForSms == true) {
                        updateProfileDetails(
                            data,
                            "N", "N"
                        )

                    } else {
                        hitApi()
                    }
                }
            } else {
                val landline = data?.personalInformation?.phoneDay
                if (landline.isNullOrEmpty().not() && landline.equals(
                        binding.inputMobileNumber.getText().toString()
                            .trim(), true
                    )
                ) {
                    findNavController().popBackStack()
                } else {


                    updateProfileDetails(
                        data,
                        data?.accountInformation?.smsOption ?: "",
                        Utils.returnMfaStatus(
                            data?.accountInformation?.mfaEnabled ?: ""
                        )
                    )
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
        if ((!NewCreateAccountRequestModel.communicationTextMessage && !NewCreateAccountRequestModel.twoStepVerification) || NewCreateAccountRequestModel.isCountryNotSupportForSms ) {
            findNavController().navigate(res, bundle)
        } else {
            hitApi()
        }
    }


    override fun onRetryClick(apiUrl: String) {

    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
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


    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?, start: Int, before: Int, count: Int
        ) {

            if (charSequence.toString().isEmpty()) {
                requiredMobileNumber = index == 0
            } else {
                val phoneNumber = binding.inputMobileNumber.getText().toString().trim()
                if (isItMobileNumber && (binding.inputCountry.getSelectedDescription()
                        .equals("UK +44", true) || binding.inputCountry.getSelectedDescription()
                        .equals(UNITED_KINGDOM, true))
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
            Constants.SMS,
            getRequiredText(binding.inputCountry.getSelectedDescription()) + binding.inputMobileNumber.getText()
                .toString().trim()
        )
        createAccountViewModel.emailVerificationApi(request)


    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {


                Log.d("sms_referenceId", Gson().toJson(resource.data?.referenceId))
                val bundle = Bundle()
                bundle.putParcelable(
                    "data", RequestOTPModel(
                        Constants.SMS,
                        binding.inputMobileNumber.getText().toString().trim()
                    )
                )
                bundle.putString(Constants.PHONE_COUNTRY_CODE,
                    binding.inputCountry.selectedItemDescription.let { getCountryCodeRequiredText(it) })
                bundle.putBoolean(
                    Constants.IS_MOBILE_NUMBER,
                    isItMobileNumber
                )

                bundle.putParcelable(
                    "response", SecurityCodeResponseModel(
                        resource.data?.emailStatusCode, 0L, resource.data?.referenceId, true
                    )
                )

                when (navFlowCall) {
                    PROFILE_MANAGEMENT_COMMUNICATION_CHANGED -> {
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    }

                    PROFILE_MANAGEMENT_MOBILE_CHANGE, PROFILE_MANAGEMENT_2FA_CHANGE -> {
                        data = navData as ProfileDetailModel?
                        if (isItMobileNumber) {
                            data?.personalInformation?.phoneCell =
                                binding.inputMobileNumber.getText().toString()
                            data?.personalInformation?.phoneCellCountryCode =
                                binding.inputCountry.selectedItemDescription.let {
                                    getCountryCodeRequiredText(it)
                                }
                        } else {
                            data?.personalInformation?.phoneDay =
                                binding.inputMobileNumber.getText().toString()
                            data?.personalInformation?.phoneDayCountryCode =
                                binding.inputCountry.selectedItemDescription.let {
                                    getCountryCodeRequiredText(it)
                                }
                        }
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    }

                    else -> {
                        bundle.putString(Constants.NAV_FLOW_KEY, ACCOUNT_CREATION_MOBILE_FLOW)
                        bundle.putString(Constants.Edit_REQUEST_KEY, navFlowCall)
                    }
                }

                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                NewCreateAccountRequestModel.sms_referenceId = resource.data?.referenceId
                Log.e("TAG", "handleEmailVerification: ")
                findNavController().navigate(
                    R.id.action_HWMobileNumberCaptureVC_to_forgotOtpFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    override fun onAutoCompleteItemClick(item: String, selected: Boolean) {
        if (selected) {
//            binding.inputMobileNumber.editText.setText("")
            binding.inputMobileNumber.removeError()
            checkIncompatibleCountry(item, 5)
                        binding.inputMobileNumber.editText.setText(binding.inputMobileNumber.editText.text.toString())

        } else {
            if (fullCountryNameWithCode.size > 0) {
                requiredCountryCode = fullCountryNameWithCode.any { it == item }
            } else {
                requiredCountryCode = item == UNITED_KINGDOM
            }
            binding.incompatibleLl.gone()
            if (requiredCountryCode) {
                checkIncompatibleCountry(item, 6)
            } else {
                binding.incompatibleLl.gone()
            }
            checkButton()
        }
    }

    private fun checkIncompatibleCountry(
        item: String,
        type: Int,
        showErrorMessage: Boolean = true
    ) {
        NewCreateAccountRequestModel.isCountryNotSupportForSms = false

        if (isItMobileNumber) {
            if (Utils.isSupportedCountry(item)) {
                NewCreateAccountRequestModel.isCountryNotSupportForSms = false
                if (showErrorMessage) {
                    binding.incompatibleLl.gone()
                }
            } else {
                NewCreateAccountRequestModel.isCountryNotSupportForSms = true
                if (showErrorMessage) {
                    binding.incompatibleLl.visible()
                    binding.incompatibleTv.text =
                        resources.getString(R.string.str_phone_number_starts_countrycode, item)

                    if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW || navFlowCall == EDIT_MOBILE || navFlowCall == EDIT_ACCOUNT_TYPE || navFlowCall == EDIT_SUMMARY) {
                        if (!NewCreateAccountRequestModel.prePay) {
                            binding.payasugoCb.visible()
                        } else {
                            binding.payasugoCb.gone()
                        }
                    } else {
                        if (accountInformationModel?.accSubType.equals(
                                Constants.BUSINESS_ACCOUNT,
                                true
                            ) || accountInformationModel?.accSubType.equals(
                                Constants.PREPAY_ACCOUNT,
                                true
                            )
                        ) {
                            binding.payasugoCb.gone()
                        } else {
                            binding.payasugoCb.visible()
                        }
                    }
                }
            }
        } else {
            NewCreateAccountRequestModel.isCountryNotSupportForSms = false
            if (showErrorMessage) {
                binding.incompatibleLl.gone()
            }
        }

    }

    private fun updateProfileDetails(
        dataModel: ProfileDetailModel?, smsOption: String, mfaEnabled: String
    ) {
        loader?.show(
            requireActivity().supportFragmentManager,
            Constants.LOADER_DIALOG
        )

        var phoneCell = dataModel?.personalInformation?.phoneCell
        var phoneCellCountryCode = dataModel?.personalInformation?.phoneCellCountryCode
        var phoneDay = dataModel?.personalInformation?.phoneDay
        var phoneDayCountryCode = dataModel?.personalInformation?.phoneDayCountryCode

        if (!isItMobileNumber) {
            phoneDay = binding.inputMobileNumber.getText().toString().trim()
            phoneDayCountryCode =
                binding.inputCountry.selectedItemDescription.let { getCountryCodeRequiredText(it) }
        } else {
            phoneCell = binding.inputMobileNumber.getText().toString().trim()
            phoneCellCountryCode =
                binding.inputCountry.selectedItemDescription.let { getCountryCodeRequiredText(it) }
        }

        if (isItMobileNumber) {
            if (NewCreateAccountRequestModel.isCountryNotSupportForSms && NewCreateAccountRequestModel.notSupportedCountrySaveDetails == false) {
                phoneCell = ""
                phoneCellCountryCode = ""
            }
        }

        val request = Utils.returnEditProfileModel(
            dataModel?.accountInformation?.businessName,
            data?.accountInformation?.fein,
            dataModel?.personalInformation?.firstName,
            dataModel?.personalInformation?.lastName,
            dataModel?.personalInformation?.addressLine1,
            dataModel?.personalInformation?.addressLine2,
            dataModel?.personalInformation?.city,
            dataModel?.personalInformation?.state,
            dataModel?.personalInformation?.zipcode,
            dataModel?.personalInformation?.zipCodePlus,
            dataModel?.personalInformation?.country,
            dataModel?.personalInformation?.emailAddress,
            dataModel?.personalInformation?.primaryEmailStatus,
            dataModel?.personalInformation?.pemailUniqueCode,
            phoneCell,
            phoneCellCountryCode,
            phoneDay,
            phoneDayCountryCode,
            dataModel?.personalInformation?.fax,
            smsOption,
            dataModel?.personalInformation?.eveningPhone,
            dataModel?.accountInformation?.stmtDelivaryMethod,
            dataModel?.accountInformation?.stmtDelivaryInterval,
            mfaEnabled,
            accountType = dataModel?.accountInformation?.accountType
        )

        viewModelProfile.updateUserDetails(request)

    }

    private fun updateProfileDetails(
        personalInformation: PersonalInformation?,
        accountInformation: AccountInformation?,
        smsOption: String,
        mfaEnabled: String
    ) {

        var phoneCell = personalInformation?.phoneCell
        var phoneCellCountryCode = personalInformation?.phoneCellCountryCode
        var phoneDay = personalInformation?.phoneDay
        var phoneDayCountryCode = personalInformation?.phoneDayCountryCode

        if (!isItMobileNumber) {
            phoneDay = binding.inputMobileNumber.getText().toString().trim()
            phoneDayCountryCode =
                binding.inputCountry.selectedItemDescription.let { getCountryCodeRequiredText(it) }
        } else {
            phoneCell = binding.inputMobileNumber.getText().toString().trim()
            phoneCellCountryCode =
                binding.inputCountry.selectedItemDescription.let { getCountryCodeRequiredText(it) }
        }


        val request = Utils.returnEditProfileModel(
            accountInformation?.businessName,
            data?.accountInformation?.fein,
            personalInformation?.firstName,
            personalInformation?.lastName,
            personalInformation?.addressLine1,
            personalInformation?.addressLine2,
            personalInformation?.city,
            personalInformation?.state,
            personalInformation?.zipcode,
            personalInformation?.zipCodePlus,
            personalInformation?.country,
            personalInformation?.emailAddress,
            personalInformation?.primaryEmailStatus,
            personalInformation?.pemailUniqueCode,
            phoneCell,
            phoneCellCountryCode,
            phoneDay,
            phoneDayCountryCode,
            personalInformation?.fax,
            smsOption,
            personalInformation?.eveningPhone,
            accountInformation?.stmtDelivaryMethod,
            accountInformation?.stmtDelivaryInterval,
            mfaEnabled,
            accountType = accountInformation?.accountType
        )

        viewModelProfile.updateUserDetails(request)

    }

}