package com.conduent.nationalhighways.ui.account.profile.email

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentChangeEmailProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.Constants.EMAIL_SELECTION_TYPE
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Utils.isEmailValid
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentChangeEmailProfile : BaseFragment<FragmentChangeEmailProfileBinding>() {

    private var btnEnabled: Boolean = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    private var data: ProfileDetailModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChangeEmailProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navData?.let {
            data = it as ProfileDetailModel
        }
    }

    override fun initCtrl() {
        binding.apply {
            edtEmail.addTextChangedListener {
                enable = isEnable()
            }

            btnNext.setOnClickListener {
                hideKeyboard()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                val request = UserNameCheckReq(binding.edtEmail.getText().toString().trim())
                viewModel.userNameAvailabilityCheck(request)
//                viewModel.emailVerificationApi(
//                    EmailVerificationRequest(
//                        selectionType = EMAIL_SELECTION_TYPE,
//                        selectionValues = binding.edtEmail.getText().toString().trim().replace(" ","") ?: ""
//                    )
//                )
            }
            isEnable()
            checkButton()
        }
    }

    fun checkButton() {
        binding?.btnNext?.isEnabled = btnEnabled
        binding?.btnNext?.isFocusable = btnEnabled
    }

    private var commaSeperatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private fun isEnable(): Boolean {
        btnEnabled =
            if (binding.edtEmail.getText().toString().trim().length > 0) {
                if (binding.edtEmail.getText().toString().trim().length < 8) {
                    false
                } else {
                    if (binding.edtEmail.getText().toString().length > 100) {
                        binding.edtEmail.setError(getString(R.string.email_address_must_be_100_characters_or_fewer))
                        false
                    } else {
                        if (!Utils.isLastCharOfStringACharacter(
                                binding.edtEmail.getText().toString().trim()
                            ) || Utils.countOccurenceOfChar(
                                binding.edtEmail.getText().toString().trim(), '@'
                            ) > 1 || binding.edtEmail.getText().toString().trim().contains(
                                Utils.TWO_OR_MORE_DOTS
                            ) || (binding.edtEmail.getText().toString().trim().last()
                                .toString().equals(".") || binding.edtEmail.getText()
                                .toString().first().toString().equals("."))
                            || (binding.edtEmail.getText().toString().trim().last().toString()
                                .equals("-") || binding.edtEmail.getText().toString().first()
                                .toString().equals("-"))
                            || (Utils.countOccurenceOfChar(
                                binding.edtEmail.getText().toString().trim(), '.'
                            ) < 1) || (Utils.countOccurenceOfChar(
                                binding.edtEmail.getText().toString().trim(), '@'
                            ) < 1)
                        ) {
                            binding.edtEmail.setError(getString(R.string.str_email_format_error_message))
                            false
                        } else {
                            if (Utils.hasSpecialCharacters(
                                    binding.edtEmail.getText().toString().trim(),
                                    Utils.splCharEmailCode
                                )
                            ) {
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.LOWER_CASE,
                                        binding.edtEmail.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.UPPER_CASE,
                                        binding.edtEmail.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.DIGITS,
                                        binding.edtEmail.getText().toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
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
                                    binding.edtEmail.setError("Email address must not include $commaSeperatedString")
                                    false
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                        binding.edtEmail.getText().toString()
                                    ).matches()
                                ) {
                                    binding.edtEmail.setError(getString(R.string.str_email_format_error_message))
                                    false
                                } else {
                                    binding.edtEmail.setError(null)
                                    true
                                }
                            } else if (!(Utils.countOccurenceOfChar(
                                    binding.edtEmail.getText().toString().trim(), '@'
                                ) > 0 && Utils.countOccurenceOfChar(
                                    binding.edtEmail.getText().toString().trim(), '@'
                                ) < 2)
                            ) {
                                binding.edtEmail.setError(getString(R.string.str_email_format_error_message))
                                false
                            } else {
                                binding.edtEmail.setError(null)
                                true
                            }
                        }
                    }
                }
            } else {
                binding.edtEmail.setError(null)
                false
            }
        return btnEnabled
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
        observe(viewModel.userNameAvailabilityCheck, ::handleEmailCheck)

    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode?.equals("500") == true) {
                    showError(
                        binding.root,
                        resource.data.message
                    )
                } else {
                    val bundle = Bundle()
                    binding.data?.referenceId = resource.data?.referenceId
                    bundle.putParcelable(DATA, binding.data)
                    bundle.putBoolean(Constants.IS_EDIT_EMAIL,arguments?.getBoolean(Constants.IS_EDIT_EMAIL) as Boolean)
                    bundle.putParcelable(
                        "response",
                        SecurityCodeResponseModel(
                            resource.data?.emailStatusCode,
                            0L,
                            resource.data?.referenceId,
                            true
                        )
                    )
                    findNavController().navigate(
                        R.id.action_change_email_profile_to_confirm_security_code,
                        bundle
                    )
                }
            }

            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                }else {
                    showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    private fun handleEmailCheck(response: Resource<Boolean?>?) {

        if (response?.data == true) {
            val request = EmailVerificationRequest(
                Constants.EMAIL,
                binding.edtEmail.getText().toString().trim()
            )
            viewModel.emailVerificationApi(request)
        } else {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW) {
                binding.edtEmail.setError(getString(R.string.an_account_with_this_email_address_already_exists))
            }

        }

    }
}