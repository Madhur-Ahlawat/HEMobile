package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPaymentRecieptMethodBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.getCountryCodeRequiredText
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.widgets.NHAutoCompleteTextview
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PaymentRecieptFragment : BaseFragment<FragmentPaymentRecieptMethodBinding>(),
    View.OnClickListener, NHAutoCompleteTextview.AutoCompleteSelectedTextListener {
    private var filterTextForSpecialChars: String? = null
    private var requiredCountryCode = true
    private var requiredMobileNumber = false
    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesCodeList: MutableList<String> = ArrayList()
    private var isItMobileNumber = true
    private var isEmailValid: Boolean = false
    private var commaSeparatedString: String? = null
    private var countriesList: MutableList<String> = ArrayList()
    private var countriesModel: List<CountriesModel?>? = ArrayList()
    private var fullCountryNameWithCode: MutableList<String> = ArrayList()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentRecieptMethodBinding =
        FragmentPaymentRecieptMethodBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {
        viewModel.getCountries()

        binding.btnContinue.setOnClickListener(this)
        loader = LoaderDialog()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                    CrossingDetailsModelsResponse::class.java
                ) != null
            ) {
                navData = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY, CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if (arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY) != null) {
                navData = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }

        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER

        if (!NewCreateAccountRequestModel.emailAddress.isNullOrEmpty()) {
            binding.apply {
                selectEmail.isChecked = true
                edtEmail.visible()
                edtEmail.editText.setText(NewCreateAccountRequestModel.emailAddress)
            }
        } else {
            binding.apply {
                selectEmail.isChecked = false
                edtEmail.gone()
            }
        }

        binding.edtEmail.editText.addTextChangedListener {
            isEnable()
            checkButton()
        }
        setMobileView()
        if (requireActivity() is CreateAccountActivity){
            (requireActivity() as CreateAccountActivity).focusToolBarCreateAccount()
        }
        else if (requireActivity() is MakeOffPaymentActivity){
            (requireActivity() as MakeOffPaymentActivity).focusMakeOffToolBar()
        }
    }

    override fun initCtrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                    CrossingDetailsModelsResponse::class.java
                ) != null
            ) {
                navData = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY, CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if (arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY) != null) {
                navData = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        binding.apply {
            selectEmail.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
//                    selectTextMessage.isChecked = false
                    edtEmail.visible()
                    isEnable()
                } else {
                    edtEmail.gone()
                }
                checkButton()
            }
            selectTextMessage.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
//                    selectEmail.isChecked = false
                    inputCountryHelper.visible()
                    inputCountry.visible()
                    inputMobileNumber.visible()
                } else {
                    inputCountry.gone()
                    inputCountryHelper.gone()
                    inputMobileNumber.gone()
                }
                checkButton()
            }
        }
    }

    override fun observer() {
        observe(viewModel.countriesList, ::handleCountriesListResponse)
        observe(viewModel.countriesCodeList, ::handleCountryCodesListResponse)
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
                    seperateCountryCodeListFromNameList(fullCountryNameWithCode)
                }


                if (navFlowCall == Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE) {
                    var userCountryCode =
                        HomeActivityMain.accountDetailsData?.personalInformation?.phoneDayCountryCode

                    if (HomeActivityMain.accountDetailsData?.personalInformation?.phoneCell.isNullOrEmpty()
                            .not()
                    ) {
                        userCountryCode =
                            HomeActivityMain.accountDetailsData?.personalInformation?.phoneCellCountryCode
                    }



                    fullCountryNameWithCode.forEachIndexed { _, fullCountryName ->
                        val countrycode = getCountryCode(fullCountryName)
                        if (countrycode == userCountryCode) {
                            binding.inputCountry.setSelectedValue(fullCountryName)
                            return@forEachIndexed
                        }
                    }
                } else {
                    if (NewCreateAccountRequestModel.countryCode?.isEmpty() == true) {
                        binding.inputCountry.setSelectedValue(Constants.UNITED_KINGDOM)
                    } else {
                        binding.inputCountry.setSelectedValue(
                            NewCreateAccountRequestModel.countryCode ?: ""
                        )
                    }
                }

                checkSupportedCountry(binding.inputCountry.selectedItemDescription)

                requiredCountryCode =
                    fullCountryNameWithCode.any { it == binding.inputCountry.selectedItemDescription }

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

    private fun handleCountriesListResponse(response: Resource<List<CountriesModel?>?>?) {
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

    private fun handleCountryCodesListResponse(response: Resource<List<CountryCodes?>?>?) {
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
                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                fullCountryNameWithCode.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })


                if (fullCountryNameWithCode.contains(Constants.UNITED_KINGDOM)) {
                    fullCountryNameWithCode.remove(Constants.UNITED_KINGDOM)
                    fullCountryNameWithCode.add(0, Constants.UNITED_KINGDOM)
                }

                if (fullCountryNameWithCode.contains(Constants.UNITED_KINGDOM)) {
                    fullCountryNameWithCode.remove(Constants.UNITED_KINGDOM)
                    fullCountryNameWithCode.add(0, Constants.UNITED_KINGDOM)
                }
                binding.apply {
                    inputCountry.dataSet.clear()
                    inputCountry.dataSet.addAll(fullCountryNameWithCode)

                    if (NewCreateAccountRequestModel.countryCode?.isNotEmpty() == true) {
                        inputCountry.setSelectedValue(
                            NewCreateAccountRequestModel.countryCode ?: ""
                        )
                    } else if (navData != null && navData is CrossingDetailsModelsResponse && !(navData as CrossingDetailsModelsResponse).fullCountryCode.isNullOrEmpty()) {
                        if (Utils.isStringOnlyInt(
                                NewCreateAccountRequestModel.mobileNumber ?: ""
                            )
                        ) {
                            inputCountry.setSelectedValue(
                                (navData as CrossingDetailsModelsResponse).fullCountryCode ?: ""
                            )
                        } else {
                            inputCountry.setSelectedValue(Constants.UNITED_KINGDOM)
                            (navData as CrossingDetailsModelsResponse).fullCountryCode =
                                Constants.UNITED_KINGDOM

                        }
                    } else {
                        inputCountry.setSelectedValue(Constants.UNITED_KINGDOM)
                        (navData as CrossingDetailsModelsResponse).fullCountryCode =
                            Constants.UNITED_KINGDOM
                    }
                }

                requiredCountryCode =
                    fullCountryNameWithCode.any { it == binding.inputCountry.selectedItemDescription }

                binding.inputCountry.clearFocus()
                binding.inputCountry.setDropDownItemSelectListener(this)

                if (!NewCreateAccountRequestModel.mobileNumber.isNullOrEmpty()) {
                    binding.apply {
                        selectTextMessage.isChecked = true
                        inputCountry.visible()
                        inputCountryHelper.visible()
                        inputMobileNumber.visible()
                        inputMobileNumber.editText.setText(NewCreateAccountRequestModel.mobileNumber)
                    }
                } else {
                    binding.apply {
                        selectTextMessage.isChecked = false
                        inputCountry.gone()
                        inputCountryHelper.gone()
                        inputMobileNumber.gone()
                    }
                }

                checkButton()
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


    private fun setMobileView() {
        isItMobileNumber = true
        binding.inputMobileNumber.setLabel(getString(R.string.mobile_number))
        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {

                /* AdobeAnalytics.setActionTrack(
                     "submit",
                     "login:forgot password:choose options:otp:new password set:password reset success",
                     "forgot password",
                     "english",
                     "login",
                     (requireActivity() as AuthActivity).previousScreen,
                     sessionManager.getLoggedInUser()
                 )*/

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                if (binding.selectEmail.isChecked) {
                    (navData as CrossingDetailsModelsResponse).recieptMode =
                        binding.edtEmail.getText().toString().trim()
                    NewCreateAccountRequestModel.emailAddress =
                        binding.edtEmail.editText.text.toString().trim()
                } else {
                    (navData as CrossingDetailsModelsResponse).recieptMode = ""
                    NewCreateAccountRequestModel.emailAddress = ""
                }
                if (binding.selectTextMessage.isChecked) {
                    if (Utils.isSupportedCountry(binding.inputCountry.selectedItemDescription)) {
                        (navData as CrossingDetailsModelsResponse).recieptMode =
                            binding.inputMobileNumber.getText().toString().trim()
                        NewCreateAccountRequestModel.mobileNumber =
                            binding.inputMobileNumber.editText.text.toString().trim()
                    } else {
                        (navData as CrossingDetailsModelsResponse).recieptMode = ""
                        NewCreateAccountRequestModel.mobileNumber = ""
                    }


                } else {
                    NewCreateAccountRequestModel.mobileNumber = ""
                }
                bundle.putParcelable(
                    Constants.NAV_DATA_KEY,
                    (navData as CrossingDetailsModelsResponse) as Parcelable?
                )

                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

                NewCreateAccountRequestModel.countryCode =
                    binding.inputCountry.getSelectedDescription()
                if (edit_summary) {
                    findNavController().navigate(
                        R.id.action_crossingRecieptFragment_editsummary_to_crossingCheckAnswersFragment,
                        bundle
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_crossingRecieptFragment_to_crossingCheckAnswersFragment,
                        bundle
                    )
                }
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
            requiredCountryCode =
                fullCountryNameWithCode.any { it == binding.inputCountry.selectedItemDescription }

            if (index == 0) {
                requiredMobileNumber = true
            }


            if (index == 1) {
                val phoneNumber = binding.inputMobileNumber.getText().toString().trim()
                if (binding.inputCountry.getSelectedDescription()
                        .equals(Constants.UNITED_KINGDOM, true)
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
                        binding.inputMobileNumber.removeError()
                        false
                    }
                } else {

                    requiredMobileNumber = if (phoneNumber.isNotEmpty()) {
                        if (phoneNumber.matches(Utils.PHONENUMBER)) {
                            binding.inputMobileNumber.removeError()
                            true
                        } else {
                            binding.inputMobileNumber.setErrorText(getString(R.string.str_non_uk_phoneNumber_error_message_tryagain))
                            false
                        }
                    } else {
                        binding.inputMobileNumber.removeError()
                        false
                    }
                }
            }
            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun isEnable(): Boolean {
        isEmailValid = if (binding.edtEmail.editText.text.toString().trim().isNotEmpty()) {
            if (binding.edtEmail.editText.text.toString().trim().length < 8) {
                binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_8_characters_or_more))
                false
            } else {
                if (binding.edtEmail.editText.text.toString().length > 100) {
                    binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_100_characters_or_fewer))
                    false
                } else {
                    if (!Utils.isLastCharOfStringACharacter(
                            binding.edtEmail.editText.text.toString().trim()
                        ) || Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.text.toString().trim(), '@'
                        ) > 1 || binding.edtEmail.editText.text.toString().trim().contains(
                            Utils.TWO_OR_MORE_DOTS
                        ) || (binding.edtEmail.editText.text.toString().trim().last()
                            .toString() == "." || binding.edtEmail.editText.text
                            .toString().first().toString() == ".")
                        || (binding.edtEmail.editText.text.toString().trim().last()
                            .toString() == "-" || binding.edtEmail.editText.text.toString()
                            .first()
                            .toString() == "-")
                        || (Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.text.toString().trim(), '.'
                        ) < 1) || (Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.text.toString().trim(), '@'
                        ) < 1)
                    ) {
                        binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                        false
                    } else {
                        if (Utils.hasSpecialCharacters(
                                binding.edtEmail.editText.text.toString().trim(),
                                Utils.splCharEmailCode
                            )
                        ) {
                            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                                Utils.LOWER_CASE,
                                binding.edtEmail.getText().toString().trim()
                            )
                            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                                Utils.UPPER_CASE,
                                binding.edtEmail.getText().toString().trim()
                            )
                            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                                Utils.DIGITS,
                                binding.edtEmail.getText().toString().trim()
                            )
                            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                                Utils.ALLOWED_CHARS_EMAIL,
                                binding.edtEmail.getText().toString().trim()
                            )
                            commaSeparatedString =
                                Utils.makeCommaSeperatedStringForPassword(
                                    Utils.removeAllCharacters(
                                        Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
                                    )
                                )
                            if (filterTextForSpecialChars!!.isNotEmpty()) {
                                binding.edtEmail.setErrorText("Email address must not include $commaSeparatedString")
                                false
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                    binding.edtEmail.getText().toString()
                                ).matches()
                            ) {
                                binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                                false
                            } else {
                                binding.edtEmail.removeError()
                                true
                            }
                        } else if (Utils.countOccurenceOfChar(
                                binding.edtEmail.editText.text.toString().trim(), '@'
                            ) !in (1..1)
                        ) {
                            binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                            false
                        } else {
                            binding.edtEmail.removeError()
                            true
                        }
                    }
                }
            }
        } else {
            binding.edtEmail.removeError()
            false
        }
        return isEmailValid
    }

    private fun checkButton() {
        if (binding.selectEmail.isChecked && binding.selectTextMessage.isChecked) {
            if (isEnable() && requiredCountryCode && requiredMobileNumber) {
                binding.btnContinue.enable()
                (navData as CrossingDetailsModelsResponse).countryCode =
                    getCountryCodeRequiredText(
                        binding.inputCountry.selectedItemDescription
                    )
                (navData as CrossingDetailsModelsResponse).fullCountryCode =
                    binding.inputCountry.selectedItemDescription
            } else {
                binding.btnContinue.disable()
            }
        } else if (binding.selectEmail.isChecked && isEnable()) {
            binding.btnContinue.enable()
        } else if (binding.selectTextMessage.isChecked && requiredCountryCode && requiredMobileNumber) {
            binding.btnContinue.enable()
            (navData as CrossingDetailsModelsResponse).countryCode =
                getCountryCodeRequiredText(
                    binding.inputCountry.selectedItemDescription
                )
            (navData as CrossingDetailsModelsResponse).fullCountryCode =
                binding.inputCountry.selectedItemDescription
        } else {
            binding.btnContinue.enable()
        }
    }

    override fun onAutoCompleteItemClick(item: String, selected: Boolean) {
        if (selected) {
            (navData as CrossingDetailsModelsResponse).countryCode =
                getCountryCodeRequiredText(item)
            (navData as CrossingDetailsModelsResponse).fullCountryCode =
                item

            requiredCountryCode = true
        } else {

            if (item.isEmpty() == true) {
                requiredCountryCode = false
            } else {
                requiredCountryCode = if (fullCountryNameWithCode.size > 0) {
                    fullCountryNameWithCode.any { it == item }
                } else {
                    false
                }

            }


            if (requiredCountryCode) {
                (navData as CrossingDetailsModelsResponse).countryCode =
                    getCountryCodeRequiredText(
                        binding.inputCountry.getSelectedDescription()
                    )
                (navData as CrossingDetailsModelsResponse).fullCountryCode =
                    binding.inputCountry.getSelectedDescription()

            } else {
                (navData as CrossingDetailsModelsResponse).countryCode = ""
                (navData as CrossingDetailsModelsResponse).fullCountryCode = ""
            }
        }

        if (item.isNotEmpty()) {
            binding.inputCountryHelper.visible()
        } else {
            binding.inputCountryHelper.invisible()
        }
        checkSupportedCountry(item)
        checkButton()
    }

    private fun checkSupportedCountry(item: String) {
        if (requiredCountryCode) {
            binding.incompatibleTv.text =
                resources.getString(R.string.str_phone_number_starts_countrycode, item)
            if (Utils.isSupportedCountry(item)) {
                binding.incompatibleLl.gone()
            } else {
                binding.incompatibleLl.visible()
            }
        }

    }

    private val countryCodesList: MutableList<String> = mutableListOf()

    private fun seperateCountryCodeListFromNameList(fullCountryNameWithCode: MutableList<String>) {
        countryCodesList.clear()
        fullCountryNameWithCode.forEachIndexed { index, s ->
            countryCodesList.add(s.substring(s.indexOf("(") + 1, s.indexOf(")")))
        }
    }

}