package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPersonalInfoNewBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern


@AndroidEntryPoint
class CreateAccountPersonalInfo : BaseFragment<FragmentCreateAccountPersonalInfoNewBinding>(),
    View.OnClickListener, OnRetryClickListener {

    var requiredFirstName = false
    var requiredLastName = false
    var requiredCompanyName = false
    var requestModel = AccountCreateRequestModel.RequestModel()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPersonalInfoNewBinding.inflate(inflater, container, false)

    override fun init() {
        binding.inputFirstName.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.inputLastName.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.inputCompanyName.editText.addTextChangedListener(GenericTextWatcher(2))

        binding.btnNext.setOnClickListener(this)

        if (NewCreateAccountRequestModel.personalAccount) {
            binding.txtCompanyName.visibility = View.GONE
            binding.inputCompanyName.visibility = View.GONE
            binding.inputFirstName.setLabel(getString(R.string.primary_account_holder_first_name))
            binding.inputLastName.setLabel(getString(R.string.primary_account_holder_last_name))

        }

        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                binding.inputFirstName.setText(NewCreateAccountRequestModel.firstName)
                binding.inputLastName.setText(NewCreateAccountRequestModel.lastName)
                binding.inputCompanyName.setText(NewCreateAccountRequestModel.companyName)
                checkButtonEnable()
            }

        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {

                NewCreateAccountRequestModel.firstName =
                    binding.inputFirstName.getText().toString()
                NewCreateAccountRequestModel.lastName =
                    binding.inputLastName.getText().toString()
                NewCreateAccountRequestModel.companyName =
                    binding.inputCompanyName.getText().toString()

                when (navFlowCall) {

                    EDIT_SUMMARY -> {
                        findNavController().popBackStack()
                    }

                    else -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        findNavController().navigate(
                            R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew,
                            bundle
                        )
                    }
                }
            }
        }
    }

    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
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

            if (NewCreateAccountRequestModel.personalAccount) {
                personalAccountErrorMessage(
                    charSequence,
                    start,
                    before,
                    count, index
                )
            } else {
                businessAccountErrorMessage(charSequence, start, before, count, index)
            }


        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButtonEnable() {
        if (requiredFirstName && requiredLastName) {
            if (!NewCreateAccountRequestModel.personalAccount) {
                if (requiredCompanyName) {
                    binding.btnNext.enable()
                } else {
                    binding.btnNext.disable()
                }
            } else {
                binding.btnNext.enable()
            }
        } else {
            binding.btnNext.disable()
        }
    }

    private fun businessAccountErrorMessage(
        charSequence: CharSequence?,
        start: Int,
        before: Int,
        count: Int,
        index: Int
    ) {
        if (index == 0) {

            if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
                binding.inputFirstName.removeError()
                // binding.inputFirstName.setErrorText(getString(R.string.enter_the_primary_account_holder_s_first_name))
                requiredFirstName = false
            } else {

                if (binding.inputFirstName.getText().toString().trim().length < 50) {

                    if (binding.inputFirstName.getText().toString().trim()
                            .contains(Utils.specialCharacter) || binding.inputFirstName.getText()
                            .toString().trim().matches(Utils.NUMBER) || hasSpecialCharacters(
                            binding.inputFirstName.getText().toString().trim()
                        )
                    ) {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_error_message))
                        requiredFirstName = false

                    } else {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    }


                } else {
                    if (binding.inputFirstName.getText().toString().trim().length > 50) {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_length_error_message))
                        requiredFirstName = false
                    } else {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    }


                }


            }

            checkButtonEnable()
        } else if (index == 1) {


            if (binding.inputLastName.getText().toString().trim().isEmpty()) {
                binding.inputLastName.removeError()
                // binding.inputLastName.setErrorText(getString(R.string.enter_the_primary_account_holder_s_last_name))
                requiredLastName = false
            } else {

                if (binding.inputLastName.getText().toString().trim().length < 50) {
                    if (binding.inputLastName.getText().toString().trim()
                            .contains(Utils.specialCharacter) || hasSpecialCharacters(
                            binding.inputLastName.text.toString().trim()
                        )
                    ) {
                        binding.inputLastName.setErrorText(getString(R.string.str_last_name_error_message))
                        requiredLastName = false

                    } else {
                        binding.inputLastName.removeError()
                        requiredLastName = true
                    }

                } else {

                    requiredLastName =
                        if (binding.inputLastName.getText().toString().trim().length > 50) {
                            binding.inputLastName.setErrorText(getString(R.string.str_first_name_length_error_message))
                            false
                        } else {
                            binding.inputLastName.removeError()
                            true
                        }
                }

            }


            checkButtonEnable()
        } else if (index == 2) {
            requiredCompanyName =
                if (binding.inputCompanyName.getText().toString().trim().isEmpty()) {
                    binding.inputCompanyName.setErrorText(getString(R.string.str_enter_the_company_name))
                    false
                } else {
                    if (binding.inputCompanyName.getText().toString().trim().length > 50) {
                        binding.inputCompanyName.setErrorText(getString(R.string.str_company_name_error_message))
                        false
                    } else if (hasSpecialCharacters(binding.inputCompanyName.getText().toString())) {
                        binding.inputCompanyName.setErrorText(getString(R.string.str_company_name_must_not_include_special_characters))
                        false
                    } else {
                        binding.inputCompanyName.removeError()
                        true
                    }

                }
        }
        checkButtonEnable()
    }

    private fun personalAccountErrorMessage(
        charSequence: CharSequence?,
        start: Int,
        before: Int,
        count: Int,
        index: Int
    ) {
        if (index == 0) {

            if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
                // binding.inputFirstName.setErrorText(getString(R.string.enter_the_primary_account_holder_s_first_name))
                requiredFirstName = false
            } else {

                if (binding.inputFirstName.getText().toString().trim().length < 50) {

                    if (binding.inputFirstName.getText().toString().trim()
                            .contains(Utils.specialCharacter)
                    ) {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_error_message))
                        requiredFirstName = false

                    } else {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    }


                } else {
                    if (binding.inputFirstName.getText().toString().trim().length > 50) {
                        binding.inputFirstName.setErrorText(getString(R.string.str_first_name_length_error_message))
                        requiredFirstName = false
                    } else {
                        binding.inputFirstName.removeError()
                        requiredFirstName = true
                    }


                }


            }

            checkButtonEnable()
        } else if (index == 1) {


            if (binding.inputLastName.getText().toString().trim().isEmpty()) {
                // binding.inputLastName.setErrorText(getString(R.string.enter_the_primary_account_holder_s_last_name))
                requiredLastName = false
            } else {

                if (binding.inputLastName.getText().toString().trim().length < 50) {
                    if (binding.inputLastName.getText().toString().trim()
                            .contains(Utils.specialCharacter)
                    ) {
                        binding.inputLastName.setErrorText(getString(R.string.str_last_name_error_message))
                        requiredLastName = false

                    } else {
                        binding.inputLastName.removeError()
                        requiredLastName = true
                    }

                } else {

                    requiredLastName =
                        if (binding.inputLastName.getText().toString().trim().length > 50) {
                            binding.inputLastName.setErrorText(getString(R.string.str_first_name_length_error_message))
                            false
                        } else {
                            binding.inputLastName.removeError()
                            true
                        }
                }

            }

        }
        checkButtonEnable()
    }
}