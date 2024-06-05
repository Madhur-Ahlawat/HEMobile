package com.conduent.nationalhighways.ui.account.profile.email

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentChangeEmailProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PERSONALDATA
import com.conduent.nationalhighways.utils.common.Constants.REPLENISHMENTINFORMATION
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FragmentChangeEmailProfile : BaseFragment<FragmentChangeEmailProfileBinding>() {

    private var btnEnabled: Boolean = false
    private val viewModel: ProfileViewModel by viewModels()
    private var data: ProfileDetailModel? = null
    private var oldEmail = ""
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChangeEmailProfileBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as ProfileDetailModel
            binding.edtEmail.editText.setText(data?.personalInformation?.userName?.lowercase(Locale.getDefault()))
            oldEmail = data?.personalInformation?.userName?.lowercase(Locale.getDefault()) ?: ""
        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).setTitle(getString(R.string.profile_email_address))
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }
    }

    override fun initCtrl() {
        binding.apply {
            edtEmail.editText.addTextChangedListener {
                enable = isEnable()
            }

            btnNext.setOnClickListener {
                hideKeyboard()
                if (oldEmail == binding.edtEmail.editText.text.toString().lowercase()) {
                    findNavController().popBackStack()
                } else {
                    showLoaderDialog()
                    val request = UserNameCheckReq(binding.edtEmail.editText.text.toString().trim())
                    viewModel.userNameAvailabilityCheck(request)
                }
            }
            isEnable()
            checkButton()
        }
    }

    fun checkButton() {
        binding.btnNext.isEnabled = btnEnabled
        binding.btnNext.isFocusable = btnEnabled
    }

    private var commaSeparatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private fun isEnable(): Boolean {
        btnEnabled =
            if (binding.edtEmail.editText.text.toString().trim().isNotEmpty()) {
                if (binding.edtEmail.editText.text.toString().trim().length < 8) {
                    binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_8_characters_or_more))
                    false
                } else {
                    if (binding.edtEmail.editText.text.toString().length > 100) {
                        false
                    } else {
                        if (!Utils.isLastCharOfStringACharacter(
                                binding.edtEmail.editText.text.toString().trim()
                            ) || Utils.countOccurrenceOfChar(
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
                            || (Utils.countOccurrenceOfChar(
                                binding.edtEmail.editText.text.toString().trim(), '.'
                            ) < 1) || (Utils.countOccurrenceOfChar(
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
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.LOWER_CASE,
                                        binding.edtEmail.editText.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.UPPER_CASE,
                                        binding.edtEmail.editText.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.DIGITS,
                                        binding.edtEmail.editText.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.ALLOWED_CHARS_EMAIL,
                                        binding.edtEmail.editText.text.toString().trim()
                                    )
                                commaSeparatedString =
                                    Utils.makeCommaSeperatedStringForPassword(
                                        Utils.removeAllCharacters(
                                            Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
                                        )
                                    )
                                if (filterTextForSpecialChars?.isNotEmpty() == true) {
                                    binding.edtEmail.setErrorText("Email address must not include $commaSeparatedString")
                                    false
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                        binding.edtEmail.editText.text.toString()
                                    ).matches()
                                ) {
                                    binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                                    false
                                } else {
                                    binding.edtEmail.removeError()
                                    true
                                }
                            } else if (!(Utils.countOccurrenceOfChar(
                                    binding.edtEmail.editText.text.toString().trim(), '@'
                                ) > 0 && Utils.countOccurrenceOfChar(
                                    binding.edtEmail.editText.text.toString().trim(), '@'
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
        return btnEnabled
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
        observe(viewModel.userNameAvailabilityCheck, ::handleEmailCheck)

    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()
                binding.data?.referenceId = resource.data?.referenceId

                bundle.putParcelable(NAV_DATA_KEY, data)
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(
                    REPLENISHMENTINFORMATION,
                    HomeActivityMain.accountDetailsData?.replenishmentInformation
                )
                bundle.putParcelable(
                    PERSONALDATA,
                    HomeActivityMain.accountDetailsData?.personalInformation?.apply {
                        emailAddress = binding.edtEmail.editText.text.toString().trim()
                    })
                bundle.putString(Constants.REFERENCE_ID, resource.data?.referenceId)
                bundle.putBoolean(
                    Constants.IS_EDIT_EMAIL,
                    arguments?.getBoolean(Constants.IS_EDIT_EMAIL) as Boolean
                )
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.AccountType_EMAIL)
                bundle.putParcelable(
                    "response",
                    SecurityCodeResponseModel(
                        resource.data?.emailStatusCode,
                        0L,
                        resource.data?.referenceId,
                        true
                    )
                )
                bundle.putParcelable(
                    "data",
                    RequestOTPModel(
                        Constants.EMAIL,
                        binding.edtEmail.editText.text.toString().trim()
                    )
                )

                findNavController().navigate(
                    R.id.action_change_email_profile_to_confirm_security_code,
                    bundle
                )

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
        viewModel._emailVerificationApiVal.postValue(null)
    }

    private fun handleEmailCheck(response: Resource<Boolean?>?) {

        if (response?.data == true) {
            val request = EmailVerificationRequest(
                Constants.EMAIL,
                binding.edtEmail.editText.text.toString().trim()
            )
            viewModel.emailVerificationApi(request)
        } else {
            dismissLoaderDialog()
            if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW || navFlowCall == Constants.PROFILE_MANAGEMENT) {
                binding.edtEmail.setErrorText(getString(R.string.an_account_with_this_email_address_already_exists))
            }

        }

    }
}