package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.databinding.FragmentEnterEmailBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_EMAIL_FLOW
import com.conduent.nationalhighways.utils.common.Constants.AccountType_EMAIL
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.FORGOT_PASSWORD_FLOW
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.ALLOWED_CHARS_EMAIL
import com.conduent.nationalhighways.utils.common.Utils.splCharEmailCode
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.toolbar
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EnterEmailFragment : BaseFragment<FragmentEnterEmailBinding>(), View.OnClickListener {

    private var commaSeparatedString: String? = null
    private var filterTextForSpecialChars: String? = null

    @Inject
    lateinit var sessionManager: SessionManager
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private val viewModelEmail: CreateAccountEmailViewModel by viewModels()
    private var isCalled = false
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var btnEnabled: Boolean = false
    private var oldEmail: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEnterEmailBinding.inflate(inflater, container, false)

    override fun init() {

        sessionManager.clearAll()
        binding.email = ""
        binding.isValid = false
        binding.edtEmail.editText.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)
        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
                if (!isViewCreated) {
                    oldEmail = NewCreateAccountRequestModel.emailAddress ?: ""
                }
                NewCreateAccountRequestModel.emailAddress?.let { binding.edtEmail.setText(it) }
                setView()
            }

            FORGOT_PASSWORD_FLOW -> {
                binding.enterDetailsTxt.text =
                    getString(R.string.forgotPassword_email_screenHeading)
                requireActivity().toolbar(getString(R.string.forgot_password))
                binding.textUsername.gone()
            }

            else -> {
                setView()
            }

        }


        isViewCreated = true

    }

    private fun setView() {
        if (NewCreateAccountRequestModel.emailAddress?.isNotEmpty() == true) {
            NewCreateAccountRequestModel.emailAddress?.let { binding.edtEmail.setText(it) }
        }

        binding.textUsername.visible()
        binding.enterDetailsTxt.text = getString(R.string.createAccount_email_screenHeading)
        requireActivity().toolbar(getString(R.string.str_create_an_account))
    }

    override fun initCtrl() {

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.confirmOption, ::handleConfirmOptionResponse)
            if (!isViewCreated) {
                observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
                observe(viewModelEmail.userNameAvailabilityCheck, ::handleEmailCheck)

            }
        }


    }

    private fun handleEmailCheck(response: Resource<Boolean?>?) {

        if (response?.data == true) {
            val request = EmailVerificationRequest(
                Constants.EMAIL,
                binding.edtEmail.getText().toString().trim()
            )
            createAccountViewModel.emailVerificationApi(request)
        } else {
            dismissLoaderDialog()
            if (navFlowCall == ACCOUNT_CREATION_EMAIL_FLOW) {
                binding.edtEmail.setErrorText(getString(R.string.an_account_with_this_email_address_already_exists))
            }

        }

    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionResponseModel?>?) {
        dismissLoaderDialog()
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    if (status.data?.statusCode?.equals("1054") == true) {
                        if (navFlowCall == FORGOT_PASSWORD_FLOW) {
                            status.data.message?.let { binding.edtEmail.setErrorText(getString(R.string.incorrect_email_try_again)) }
                        }
                    } else {
                        binding.root.post {
                            val bundle = Bundle()
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                            bundle.putParcelable(Constants.OPTIONS, status.data)
                            findNavController().navigate(
                                R.id.action_forgotPasswordFragment_to_chooseOptionFragment,
                                bundle
                            )
                        }
                    }
                    if (requireActivity() is AuthActivity) {
                        AdobeAnalytics.setActionTrackError(
                            "next",
                            "login:forgot password",
                            "forgot password",
                            "english",
                            "login",
                            (requireActivity() as AuthActivity).previousScreen, "success",
                            sessionManager.getLoggedInUser()
                        )
                    }
                }

                is Resource.DataError -> {
                    if (checkSessionExpiredOrServerError(status.errorModel)) {
                        displaySessionExpireDialog(status.errorModel)
                    } else {
                        binding.edtEmail.setErrorText(status.errorMsg)
                        if (requireActivity() is AuthActivity) {
                            AdobeAnalytics.setActionTrackError(
                                "next",
                                "login:forgot password",
                                "forgot password",
                                "english",
                                "login",
                                (requireActivity() as AuthActivity).previousScreen, status.errorMsg,
                                sessionManager.getLoggedInUser()
                            )
                        }
                    }

                }

                else -> {
                }
            }
            isCalled = false
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {

                hideKeyboard()

                val emailText = binding.edtEmail.getText().toString().trim()

                when (navFlowCall) {

                    EDIT_SUMMARY -> {
                        handleEditNavigation(emailText)
                    }

                    EDIT_ACCOUNT_TYPE -> {
                        handleAccountEditNavigation(emailText)
                    }

                    FORGOT_PASSWORD_FLOW -> {
                        showLoaderDialog()
                        sessionManager.saveAccountNumber(emailText)
                        isCalled = true
                        viewModel.confirmOptionForForgot(emailText)
                    }

                    else -> {
                        NewCreateAccountRequestModel.emailAddress = emailText
                        checkEmailAddress()
                    }

                }
            }
        }
    }

    private fun handleEditNavigation(emailText: String) {
        if (emailText == oldEmail) {
            findNavController().popBackStack()
        } else {
            NewCreateAccountRequestModel.emailAddress = emailText
            checkEmailAddress()
        }
    }

    private fun handleAccountEditNavigation(emailText: String) {
        if (emailText == oldEmail) {
            val bundle = Bundle()
            bundle.putString(
                Constants.NAV_FLOW_KEY,
                navFlowCall
            )
            findNavController().navigate(
                R.id.action_forgotPasswordFragment_to_createPasswordFragment,
                bundle
            )
        } else {
            NewCreateAccountRequestModel.emailAddress = emailText
            checkEmailAddress()
        }
    }


    private fun isEnable() {
        btnEnabled = if (binding.edtEmail.editText.getText().toString().trim().isNotEmpty()) {
            if (binding.edtEmail.editText.getText().toString().trim().length < 8) {
                binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_8_characters_or_more))
                false
            } else {
                if (binding.edtEmail.editText.getText().toString().length > 100) {
                    binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_100_characters_or_fewer))
                    false
                } else {
                    if (!Utils.isLastCharOfStringACharacter(
                            binding.edtEmail.editText.getText().toString().trim()
                        ) || Utils.countOccurrenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '@'
                        ) > 1 || binding.edtEmail.editText.getText().toString().trim().contains(
                            Utils.TWO_OR_MORE_DOTS
                        ) || (binding.edtEmail.editText.getText().toString().trim().last()
                            .toString() == "." || binding.edtEmail.editText.text
                            .toString().first().toString() == ".")
                        || (binding.edtEmail.editText.getText().toString().trim().last()
                            .toString() == "-" || binding.edtEmail.editText.getText().toString()
                            .first()
                            .toString() == "-")
                        || (Utils.countOccurrenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '.'
                        ) < 1) || (Utils.countOccurrenceOfChar(
                            binding.edtEmail.editText.getText().toString().trim(), '@'
                        ) < 1)
                    ) {
                        binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                        false
                    } else {
                        if (Utils.hasSpecialCharacters(
                                binding.edtEmail.editText.getText().toString().trim(),
                                splCharEmailCode
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
                                ALLOWED_CHARS_EMAIL,
                                binding.edtEmail.getText().toString().trim()
                            )
                            commaSeparatedString =
                                Utils.makeCommaSeperatedStringForPassword(
                                    Utils.removeAllCharacters(
                                        ALLOWED_CHARS_EMAIL, filterTextForSpecialChars ?: ""
                                    )
                                )
                            if (filterTextForSpecialChars?.isNotEmpty() == true) {
                                binding.edtEmail.setErrorText(
                                    resources.getString(
                                        R.string.str_email_disallowed_character,
                                        commaSeparatedString
                                    )
                                )
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
                        } else if (Utils.countOccurrenceOfChar(
                                binding.edtEmail.editText.getText().toString().trim(), '@'
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
        checkButton()
    }

    private fun checkButton() {
        binding.btnNext.isEnabled = btnEnabled
        binding.btnNext.isFocusable = btnEnabled
    }

    private fun checkEmailAddress() {
        showLoaderDialog()
        val request = UserNameCheckReq(binding.edtEmail.getText().toString().trim())
        viewModelEmail.userNameAvailabilityCheck(request)
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        dismissLoaderDialog()
        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()
                bundle.putParcelable(
                    "data",
                    RequestOTPModel(Constants.EMAIL, binding.edtEmail.getText().toString().trim())
                )

                bundle.putParcelable(
                    "response",
                    SecurityCodeResponseModel(
                        resource.data?.emailStatusCode,
                        0L,
                        resource.data?.referenceId,
                        true
                    )
                )


                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, AccountType_EMAIL)

                NewCreateAccountRequestModel.referenceId = resource.data?.referenceId
                findNavController().navigate(
                    R.id.action_forgotPasswordFragment_to_forgotOtpFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    binding.edtEmail.setErrorText(resource.errorMsg)

                }
            }

            else -> {
            }
        }
    }


}