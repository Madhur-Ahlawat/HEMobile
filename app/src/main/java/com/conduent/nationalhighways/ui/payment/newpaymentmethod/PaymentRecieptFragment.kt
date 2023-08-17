package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentForgotResetBinding
import com.conduent.nationalhighways.databinding.FragmentPaymentRecieptMethodBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_2FA_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_ADDRESS_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_COMMUNICATION_CHANGED
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_MOBILE_CHANGE
import com.conduent.nationalhighways.utils.common.Constants.REMOVE_VEHICLE
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PaymentRecieptFragment : BaseFragment<FragmentPaymentRecieptMethodBinding>(),
    View.OnClickListener, DropDownItemSelectListener {
    private var commaSeperatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private var requiredCountryCode = false
    private var requiredMobileNumber = false
    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesCodeList: MutableList<String> = ArrayList()
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var isItMobileNumber = true
    private var btnEnabled: Boolean = false
    private val viewModelProfile: ProfileViewModel by viewModels()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentRecieptMethodBinding =
        FragmentPaymentRecieptMethodBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {
        binding.btnContinue.setOnClickListener(this)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.inputCountry.dropDownItemSelectListener = this
        binding.edtEmail.editText.addTextChangedListener {
            isEnable()
        }
    }

    private fun isEnable() {
        btnEnabled = if (binding.edtEmail.editText.getText().toString().trim().length > 0) {
            if (binding.edtEmail.editText.getText().toString().trim().length < 8) {
                false
            } else {
                if (binding.edtEmail.editText.getText().toString().length > 100) {
                    binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_100_characters_or_fewer))
                    false
                } else {
                    if (!Utils.isLastCharOfStringACharacter(
                            binding.edtEmail.editText.getText().toString().trim()
                        ) || Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '@'
                        ) > 1 || binding.edtEmail.editText.getText().toString().trim().contains(
                            Utils.TWO_OR_MORE_DOTS
                        ) || (binding.edtEmail.editText.getText().toString().trim().last()
                            .toString().equals(".") || binding.edtEmail.editText.getText()
                            .toString().first().toString().equals("."))
                        || (binding.edtEmail.editText.getText().toString().trim().last().toString()
                            .equals("-") || binding.edtEmail.editText.getText().toString().first()
                            .toString().equals("-"))
                        || (Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '.'
                        ) < 1) || (Utils.countOccurenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '@'
                        ) < 1)
                    ) {
                        binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                        false
                    } else {
                        if (Utils.hasSpecialCharacters(
                                binding.edtEmail.editText.getText().toString().trim(),
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
                            commaSeperatedString =
                                Utils.makeCommaSeperatedStringForPassword(
                                    Utils.removeAllCharacters(
                                        Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
                                    )
                                )
                            if (filterTextForSpecialChars!!.length > 0) {
                                binding.edtEmail.setErrorText("Email address must not include $commaSeperatedString")
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
                        } else if (!(Utils.countOccurenceOfChar(
                                binding.edtEmail.editText.getText().toString().trim(), '@'
                            ) > 0 && Utils.countOccurenceOfChar(
                                binding.edtEmail.editText.getText().toString().trim(), '@'
                            ) < 2)
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
        checkButtonEmail()
    }

    override fun initCtrl() {
        binding?.apply {
            selectEmail.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectTextMessage.isChecked = !isChecked
                    edtEmail.visible()
                    isEnable()
                } else {
                    edtEmail.gone()
                }
            }
            selectTextMessage.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    selectEmail.isChecked = !isChecked
                    inputCountry.visible()
                    inputMobileNumber.visible()
                    checkButton()
                } else {
                    inputCountry.gone()
                    inputMobileNumber.gone()
                }

            }
            setMobileView()
        }
    }

    override fun observer() {
        viewModel.getCountryCodesList()
        observe(viewModel.countriesCodeList, ::getCountryCodesList)
    }

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesCodeList.clear()
                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                countriesCodeList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
                if (countriesCodeList.contains(Constants.UK_CODE)) {
                    countriesCodeList.remove(Constants.UK_CODE)
                    countriesCodeList.add(0, Constants.UK_CODE)
                }
                binding.apply {
                    inputCountry.dataSet.addAll(countriesCodeList)
                    inputCountry.setSelectedValue(Constants.UK_CODE)
                }
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {
            }

        }
    }

    private fun setMobileView() {
        isItMobileNumber = true
        binding.inputMobileNumber.setLabel(getString(R.string.str_mobile_number))
        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {

                    /* AdobeAnalytics.setActionTrack(
                         "submit",
                         "login:forgot password:choose options:otp:new password set:password reset success",
                         "forgot password",
                         "english",
                         "login",
                         (requireActivity() as AuthActivity).previousScreen,
                         sessionManager.getLoggedInUser()
                     )*/

//                val bundle = Bundle()
//                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
//                NewCreateAccountRequestModel.password =
//                    binding.edtNewPassword.getText().toString().trim()
//                findNavController().navigate(
//                    R.id.action_createPasswordFragment_to_optForSmsFragment,
//                    bundle
//                )

                    requireActivity().startNormalActivity(LoginActivity::class.java)
                    requireActivity().finish()

                }

            }
    }

    private fun getSpannedText(text: String): Spanned? {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
        TODO("Not yet implemented")
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        TODO("Not yet implemented")
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

            if (index == 0) {
                requiredMobileNumber = true
            }


            if (index == 1) {
                val phoneNumber = binding.inputMobileNumber.getText().toString().trim()
                if (binding.inputCountry.getSelectedDescription().equals("UK +44", true)) {
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
                            binding.inputMobileNumber.setErrorText(getString(R.string.str_non_uk_phoneNumber_error_message))
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
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }

    private fun checkButtonEmail() {
        if (btnEnabled && binding.selectEmail.isChecked) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }
}