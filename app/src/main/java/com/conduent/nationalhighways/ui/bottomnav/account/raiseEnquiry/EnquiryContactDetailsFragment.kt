package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.databinding.FragmentEnquiryContactDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.widgets.NHAutoCompleteTextview
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EnquiryContactDetailsFragment : BaseFragment<FragmentEnquiryContactDetailsBinding>(),
    BackPressListener, NHAutoCompleteTextview.AutoCompleteSelectedTextListener {

    @Inject
    lateinit var sm: SessionManager
    private val createaccountViewmodel: CreateAccountPostCodeViewModel by viewModels()
    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()

    private var fullCountryNameWithCode: MutableList<String> = ArrayList()
    private var requiredCountryCode = false
    private var countriesCodeList: MutableList<String> = ArrayList()
    private var loader: LoaderDialog? = null
    private var requiredFirstName = false
    private var requiredLastName = false
    private var requiredEmail = false
    private var commaSeparatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private var requiredMobileNumber: Boolean = true
    private var countriesList: MutableList<String> = ArrayList()
    private var countriesModel: List<CountriesModel?>? = ArrayList()
    var isViewCreated: Boolean = false
    private var editRequest: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEnquiryContactDetailsBinding =
        FragmentEnquiryContactDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.Edit_REQUEST_KEY) == true) {
            editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        }

        setBackPressListener(this)

        createaccountViewmodel.getCountries()
        binding.btnNext.setOnClickListener {

            saveData()
            when (editRequest) {
                Constants.EDIT_SUMMARY -> {
                    findNavController().navigate(
                        R.id.action_contactdetails_enquiryContactDetailsFragment_to_enquirySummaryFragment,
                        getBundleData()
                    )
                }

                Constants.EDIT_CATEGORY_DATA -> {
                    findNavController().navigate(
                        R.id.action_categoryChange_enquiryContactDetailsFragment_to_enquirySummaryFragment,
                        getBundleData()
                    )
                }

                Constants.EDIT_COMMENTS_DATA -> {
                    findNavController().navigate(
                        R.id.action_commentsChange_enquiryContactDetailsFragment_to_enquirySummaryFragment,
                        getBundleData()
                    )
                }

                else -> {
                    findNavController().navigate(
                        R.id.action_enquiryContactDetailsFragment_to_enquirySummaryFragment,
                        getBundleData()
                    )

                }
            }
        }
        listeners()

        if (!backButton) {
            if (requireActivity() is RaiseEnquiryActivity) {
                (requireActivity() as RaiseEnquiryActivity).hideBackIcon()
            } else if (requireActivity() is HomeActivityMain) {
                (requireActivity() as HomeActivityMain).hideBackIcon()
            }
        }

    }

    private fun saveData() {
        viewModel.edit_enquiryModel.value?.firstname = binding.firstnameEt.getText().toString()
        viewModel.edit_enquiryModel.value?.lastname = binding.lastnameEt.getText().toString()
        viewModel.edit_enquiryModel.value?.email = binding.emailEt.getText().toString()
        viewModel.edit_enquiryModel.value?.mobileNumber =
            binding.mobileNumberEt.getText().toString().trim()
    }

    private fun getBundleData(): Bundle {
        val bundle = Bundle()
        if (editRequest == Constants.EDIT_SUMMARY) {
            bundle.putString(Constants.Edit_REQUEST_KEY, Constants.EDIT_CONTACT_DETAILS_DATA)
        } else {
            bundle.putString(Constants.Edit_REQUEST_KEY, editRequest)
        }
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        return bundle
    }

    private fun listeners() {
        binding.firstnameEt.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.lastnameEt.editText.addTextChangedListener(GenericTextWatcher(3))
        binding.emailEt.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.mobileNumberEt.editText.addTextChangedListener(GenericTextWatcher(2))
    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?, start: Int, before: Int, count: Int
        ) {

            contactDetailsErrorMessage(
                index
            )
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun contactDetailsErrorMessage(
        index: Int
    ) {
        when (index) {
            0 -> {
                if (binding.firstnameEt.getText().toString().trim().isEmpty()) {
                    binding.firstnameEt.removeError()
                    requiredFirstName = false
                } else {
                    if (binding.firstnameEt.getText().toString().trim().length <= 50) {
                        requiredFirstName =
                            if (binding.firstnameEt.getText().toString().trim().replace(" ", "")
                                    .matches(
                                        Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME
                                    )
                            ) {
                                binding.firstnameEt.removeError()
                                true
                            } else {
                                binding.firstnameEt.setErrorText(getString(R.string.str_first_name_error_message))
                                false
                            }
                    } else {
                        requiredFirstName = false
                        binding.firstnameEt.setErrorText(getString(R.string.str_first_name_length_error_message))
                    }
                }

                checkButton()
            }

            3 -> {

                if (binding.lastnameEt.getText().toString().trim().isEmpty()) {
                    binding.lastnameEt.removeError()
                    requiredLastName = false
                } else {
                    if (binding.lastnameEt.getText().toString().trim().length <= 50) {
                        requiredLastName =
                            if (binding.lastnameEt.getText().toString().trim().replace(" ", "")
                                    .matches(
                                        Utils.ACCOUNT_NAME_FIRSTNAME_LASTNAME
                                    )
                            ) {
                                binding.lastnameEt.removeError()
                                true
                            } else {
                                binding.lastnameEt.setErrorText(getString(R.string.str_last_name_error_message))
                                false
                            }
                    } else {
                        requiredLastName = false
                        binding.lastnameEt.setErrorText(getString(R.string.str_last_name_length_error_message))
                    }
                }

                checkButton()
            }

            1 -> {
                requiredEmail = if (binding.emailEt.editText.text.toString().trim().isNotEmpty()) {

                    if (binding.emailEt.editText.text.toString().length > 64) {
                        binding.emailEt.setErrorText(getString(R.string.email_address_must_be_100_characters_or_fewer))
                        false
                    } else {
                        if (!Utils.isLastCharOfStringACharacter(
                                binding.emailEt.editText.text.toString().trim()
                            ) || Utils.countOccurenceOfChar(
                                binding.emailEt.editText.text.toString().trim(), '@'
                            ) > 1 || binding.emailEt.editText.text.toString().trim().contains(
                                Utils.TWO_OR_MORE_DOTS
                            ) || (binding.emailEt.editText.text.toString().trim().last()
                                .toString() == "." || binding.emailEt.editText.text.toString()
                                .first()
                                .toString() == ".") || (binding.emailEt.editText.text.toString()
                                .trim().last()
                                .toString() == "-" || binding.emailEt.editText.text.toString()
                                .first().toString() == "-") || (Utils.countOccurenceOfChar(
                                binding.emailEt.editText.text.toString().trim(), '.'
                            ) < 1) || (Utils.countOccurenceOfChar(
                                binding.emailEt.editText.text.toString().trim(), '@'
                            ) < 1)
                        ) {
                            binding.emailEt.setErrorText(getString(R.string.str_email_format_error_message))
                            false
                        } else {
                            if (Utils.hasSpecialCharacters(
                                    binding.emailEt.editText.text.toString().trim(),
                                    Utils.splCharEmailCode
                                )
                            ) {
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.LOWER_CASE,
                                        binding.emailEt.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.UPPER_CASE,
                                        binding.emailEt.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.DIGITS,
                                        binding.emailEt.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.ALLOWED_CHARS_EMAIL,
                                        binding.emailEt.getText().toString().trim()
                                    )
                                commaSeparatedString =
                                    Utils.makeCommaSeperatedStringForPassword(
                                        Utils.removeAllCharacters(
                                            Utils.ALLOWED_CHARS_EMAIL,
                                            filterTextForSpecialChars!!
                                        )
                                    )
                                if (filterTextForSpecialChars!!.isNotEmpty()) {
                                    binding.emailEt.setErrorText("Email address must not include $commaSeparatedString")
                                    false
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                        binding.emailEt.getText().toString()
                                    ).matches()
                                ) {
                                    binding.emailEt.setErrorText(getString(R.string.str_email_format_error_message))
                                    false
                                } else {
                                    binding.emailEt.removeError()
                                    true
                                }
                            } else if (Utils.countOccurenceOfChar(
                                    binding.emailEt.editText.text.toString().trim(), '@'
                                ) !in (1..1)
                            ) {
                                binding.emailEt.setErrorText(getString(R.string.str_email_format_error_message))
                                false
                            } else {
                                binding.emailEt.removeError()
                                true
                            }
                        }
                    }
                } else {
                    binding.emailEt.removeError()
                    false
                }
                checkButton()
            }

            2 -> {
                val phoneNumber = binding.mobileNumberEt.getText().toString().trim()



                if (phoneNumber.isNotEmpty()) {
                    Log.e("TAG", "contactDetailsErrorMessage: phoneNumber "+phoneNumber )
                    Log.e("TAG", "contactDetailsErrorMessage: countrycode "+binding.countrycodeEt.getSelectedDescription() )

                    if (binding.countrycodeEt.getSelectedDescription()
                            .equals(Constants.UNITED_KINGDOM, true)
                    ) {
                        requiredMobileNumber =
                            if (phoneNumber.matches(Utils.UK_MOBILE_REGEX)) {
                                binding.mobileNumberEt.removeError()
                                true
                            } else {
                                binding.mobileNumberEt.setErrorText(getString(R.string.str_uk_phoneNumber_error_message))
                                false
                            }

                    } else {
                        requiredMobileNumber =
                            if (phoneNumber.matches(Utils.PHONENUMBER)) {
                                binding.mobileNumberEt.removeError()
                                true
                            } else {
                                binding.mobileNumberEt.setErrorText(getString(R.string.str_non_uk_phoneNumber_error_message))
                                false
                            }

                    }

                }else{
                    binding.mobileNumberEt.removeError()
                    requiredCountryCode=true
                    requiredMobileNumber=true
                }

                checkButton()

            }
        }
    }

    private fun checkButton() {
        if (requiredEmail && requiredFirstName && requiredCountryCode && requiredMobileNumber && requiredLastName) {
            binding.btnNext.enable()
        } else {
            binding.btnNext.disable()
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
        if (!isViewCreated) {
            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            observe(createaccountViewmodel.countriesCodeList, ::getCountryCodesList)
            observe(createaccountViewmodel.countriesList, ::getCountriesList)
        }
        isViewCreated = true

    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {/* if (loader?.isVisible == true) {
             loader?.dismiss()
         }*/
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                countriesModel = response.data
                createaccountViewmodel.getCountryCodesList()

                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                countriesList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })

                if (countriesList.contains(Constants.UK_COUNTRY)) {
                    countriesList.remove(Constants.UK_COUNTRY)
                    countriesList.add(0, Constants.UK_COUNTRY)
                }
            }

            is Resource.DataError -> {
                if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && response.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
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

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                fullCountryNameWithCode.clear()
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

                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                fullCountryNameWithCode.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })

                if (fullCountryNameWithCode.contains(Constants.UNITED_KINGDOM)) {
                    fullCountryNameWithCode.remove(Constants.UNITED_KINGDOM)
                    fullCountryNameWithCode.add(0, Constants.UNITED_KINGDOM)
                }
                binding.countrycodeEt.dataSet.addAll(fullCountryNameWithCode)

                var countryCode = viewModel.edit_enquiryModel.value?.fullcountryCode ?: ""
                if (viewModel.edit_enquiryModel.value?.fullcountryCode?.isEmpty() == true) {
                    countryCode = Constants.UNITED_KINGDOM
                    binding.apply {
                        countrycodeEt.setSelectedValue(Constants.UNITED_KINGDOM)
                    }
                } else {
                    binding.countrycodeEt.setSelectedValue(
                        viewModel.edit_enquiryModel.value?.fullcountryCode ?: ""
                    )
                }

                checkRequireCountryCode()

                viewModel.edit_enquiryModel.value?.countryCode = getCountryCode(countryCode)
                viewModel.edit_enquiryModel.value?.fullcountryCode = countryCode

                setSavedData()

                binding.countrycodeEt.clearFocus()
                binding.countrycodeEt.setDropDownItemSelectListener(this)

            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }

    private fun checkRequireCountryCode() {
        if (binding.mobileNumberEt.getText().toString().trim().isNotEmpty()) {
            requiredCountryCode =
                fullCountryNameWithCode.any { it == binding.countrycodeEt.selectedItemDescription }
        } else {
            requiredCountryCode = true
        }
    }

    private fun setSavedData() {
        if (editRequest == Constants.EDIT_SUMMARY || viewModel.edit_enquiryModel.value?.firstname?.isNotEmpty() == true) {
            binding.firstnameEt.setText(viewModel.edit_enquiryModel.value?.firstname ?: "")
            binding.lastnameEt.setText(viewModel.edit_enquiryModel.value?.lastname ?: "")
            binding.emailEt.setText(viewModel.edit_enquiryModel.value?.email ?: "")
            binding.countrycodeEt.setSelectedValue(
                viewModel.edit_enquiryModel.value?.fullcountryCode ?: ""

            )
            binding.mobileNumberEt.setText(viewModel.edit_enquiryModel.value?.mobileNumber ?: "")

        } else {
            if (navFlowFrom == Constants.ACCOUNT_CONTACT_US || navFlowFrom == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
                binding.firstnameEt.setText(Utils.capitalizeString(sm.fetchFirstName()) ?: "")
                binding.lastnameEt.setText(Utils.capitalizeString(sm.fetchLastName()) ?: "")
                binding.emailEt.setText(sm.fetchAccountEmailId() ?: "")

                val userCountryCode = sm.fetchUserCountryCode()
                var fullCountryNameToSave = ""
                fullCountryNameWithCode.forEachIndexed { _, fullCountryName ->
                    val countrycode = getCountryCode(fullCountryName)
                    if (countrycode == userCountryCode) {
                        fullCountryNameToSave = fullCountryName
                        binding.countrycodeEt.setSelectedValue(fullCountryName)
                        return@forEachIndexed
                    }
                }
                if (userCountryCode?.isNotEmpty() == true) {
                    viewModel.edit_enquiryModel.value?.countryCode = userCountryCode
                    viewModel.edit_enquiryModel.value?.fullcountryCode = fullCountryNameToSave
                }
                binding.mobileNumberEt.setText(sm.fetchUserMobileNUmber() ?: "")

            }

        }
    }


    private fun getCountryCode(selectedItem: String): String {
        val data = selectedItem
        val openingParenIndex = selectedItem.indexOf("(")
        val closingParenIndex = selectedItem.indexOf(")")

        val extractedText =
            if (openingParenIndex != -1 && closingParenIndex != -1 && closingParenIndex > openingParenIndex) {
                data.substring(openingParenIndex + 1, closingParenIndex)
            } else {
                ""
            }
        return extractedText
    }

    override fun onBackButtonPressed() {
        saveOriginalDataToEditModel()
    }

    private fun saveOriginalDataToEditModel() {
        if (editRequest == Constants.EDIT_SUMMARY) {

            viewModel.edit_enquiryModel.value?.firstname =
                viewModel.enquiryModel.value?.firstname ?: ""
            viewModel.edit_enquiryModel.value?.lastname =
                viewModel.enquiryModel.value?.lastname ?: ""
            viewModel.edit_enquiryModel.value?.email = viewModel.enquiryModel.value?.email ?: ""
            viewModel.edit_enquiryModel.value?.mobileNumber =
                viewModel.enquiryModel.value?.mobileNumber ?: ""
            viewModel.edit_enquiryModel.value?.countryCode =
                viewModel.enquiryModel.value?.countryCode ?: ""
            viewModel.edit_enquiryModel.value?.fullcountryCode =
                viewModel.enquiryModel.value?.fullcountryCode ?: ""

            viewModel.edit_enquiryModel.value?.category =
                viewModel.enquiryModel.value?.category ?: CaseCategoriesModel("", "")
            viewModel.edit_enquiryModel.value?.subCategory =
                viewModel.enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")

            viewModel.edit_enquiryModel.value?.comments =
                viewModel.enquiryModel.value?.comments ?: ""
            viewModel.edit_enquiryModel.value?.file = viewModel.enquiryModel.value?.file ?: File("")
            viewModel.edit_enquiryModel.value?.fileName =
                viewModel.enquiryModel.value?.fileName ?: ""
            viewModel.edit_enquiryModel.value?.apiFileName =
                viewModel.enquiryModel.value?.apiFileName ?: ""
        }
    }

    override fun onAutoCompleteItemClick(item: String, selected: Boolean) {
        if (selected) {
            viewModel.edit_enquiryModel.value?.countryCode = getCountryCode(item)
            viewModel.edit_enquiryModel.value?.fullcountryCode = item

            binding.mobileNumberEt.setText("")
            binding.mobileNumberEt.removeError()
        } else {
            if (binding.mobileNumberEt.getText().toString().trim().isNotEmpty()) {
                if (fullCountryNameWithCode.size > 0) {
                    this.requiredCountryCode = fullCountryNameWithCode.any { it == item }
                } else {
                    requiredCountryCode = false
                }
                if (requiredCountryCode) {
                    saveCountryCodeToViewModel(item)
                } else {
                    saveCountryCodeToViewModel("")
                }
            } else {
                saveCountryCodeToViewModel("")
                requiredCountryCode = true
            }

        }
        checkButton()

    }

    fun saveCountryCodeToViewModel(item: String) {
        viewModel.edit_enquiryModel.value?.countryCode = getCountryCode(item)
        viewModel.edit_enquiryModel.value?.fullcountryCode = item

    }


}