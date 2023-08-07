package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
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
import com.conduent.nationalhighways.databinding.ForgotpasswordChangesBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNT_CREATION_EMAIL_FLOW
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
class ForgotPasswordFragment : BaseFragment<ForgotpasswordChangesBinding>(), View.OnClickListener {

    private var commaSeperatedString: String? = null
    private var filterTextForSpecialChars: String? = null

    @Inject
    lateinit var sessionManager: SessionManager
    private var loader: LoaderDialog? = null
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private val viewModelEmail: CreateAccountEmailViewModel by viewModels()
    private var isCalled = false
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var btnEnabled: Boolean = false


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ForgotpasswordChangesBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.clearAll()
//        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()


        // binding.model = ConfirmOptionModel(identifier = "", enable = false)
        binding.email = ""
        binding.isValid = false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.edtEmail.editText.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)

        when (navFlowCall) {

            EDIT_ACCOUNT_TYPE, EDIT_SUMMARY -> {
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

        /*AdobeAnalytics.setScreenTrack(
            "login:forgot password",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password",
            sessionManager.getLoggedInUser()
        )*/

    }

    private fun setView() {
        binding.textUsername.visible()
        binding.enterDetailsTxt.text = getString(R.string.createAccount_email_screenHeading)
        requireActivity().toolbar(getString(R.string.str_create_an_account))
    }

    override fun initCtrl() {
        //binding.edtPostcode.addTextChangedListener { isEnable() }

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.confirmOption, ::handleConfirmOptionResponse)
        }
        if (!isViewCreated) {
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
            observe(viewModelEmail.userNameAvailabilityCheck, :: handleEmailCheck)

        }

        isViewCreated = true
    }

    private fun handleEmailCheck(response : Resource<Boolean?>?){

        if(response?.data == true){
            val request = EmailVerificationRequest(
                Constants.EMAIL,
                binding.edtEmail.getText().toString().trim()
            )
            createAccountViewModel.emailVerificationApi(request)
        }else{
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            binding.edtEmail.setErrorText(getString(R.string.an_account_with_this_email_address_already_exists))

        }

    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    if (status.data?.statusCode?.equals("1054") == true) {
                        status.data.message?.let { binding.edtEmail.setErrorText(it) }
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

                is Resource.DataError -> {
                    binding.edtEmail.setErrorText(status.errorMsg)

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

                else -> {
                }
            }
            isCalled = false
        }
    }

    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (status) {
            is Resource.Success -> {
                val bundle = Bundle()
                bundle.putParcelable("data", RequestOTPModel(Constants.EMAIL, binding.email))

                bundle.putParcelable("response", status.data)


                bundle.putString(Constants.NAV_FLOW_KEY, ACCOUNT_CREATION_EMAIL_FLOW)
                bundle.putString(Constants.Edit_REQUEST_KEY, navFlowCall)
                findNavController().navigate(
                    R.id.action_forgotPasswordFragment_to_otpFragment,
                    bundle
                )

                AdobeAnalytics.setActionTrack2(
                    "continue",
                    "login:forgot password:choose options",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, Constants.EMAIL,
                    sessionManager.getLoggedInUser()
                )


            }

            is Resource.DataError -> {
                binding.edtEmail.setErrorText(status.errorMsg)
            }

            else -> {
            }

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
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        sessionManager.saveAccountNumber(emailText)
                        isCalled = true
                        viewModel.confirmOptionForForgot(emailText)}
                    else -> {NewCreateAccountRequestModel.emailAddress = emailText
                        checkEmailAddress()}

                }
            }
        }
    }

    private fun handleEditNavigation(emailText: String) {
        if (emailText == NewCreateAccountRequestModel.emailAddress) {
                findNavController().popBackStack()
        } else {
            NewCreateAccountRequestModel.emailAddress = emailText
            checkEmailAddress()
        }
    }

    private fun handleAccountEditNavigation(emailText: String) {
        if (emailText == NewCreateAccountRequestModel.emailAddress) {
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
        btnEnabled = if (binding.edtEmail.editText.getText().toString().trim().length > 0) {
            if (binding.edtEmail.editText.getText().toString().trim().length < 8) {
                false
            }
            else {
                if(binding.edtEmail.editText.getText().toString().length>100){
                    binding.edtEmail.setErrorText(getString(R.string.email_address_must_be_100_characters_or_fewer))
                    false
                }
                else{
                    if ( !Utils.isLastCharOfStringACharacter(binding.edtEmail.editText.getText().toString().trim()) || Utils.countOccurenceOfChar(binding.edtEmail.editText.getText().toString().trim(),'@')>1 || binding.edtEmail.editText.getText().toString().trim().contains(
                            Utils.TWO_OR_MORE_DOTS
                        ) || (binding.edtEmail.editText.getText().toString().trim().last()
                            .toString().equals(".") || binding.edtEmail.editText.getText()
                            .toString().first().toString().equals("."))
                        || (binding.edtEmail.editText.getText().toString().trim().last().toString()
                            .equals("-") || binding.edtEmail.editText.getText().toString().first()
                            .toString().equals("-"))
                        || (Utils.countOccurenceOfChar(binding.edtEmail.editText.getText().toString().trim(),'.')<1) || (Utils.countOccurenceOfChar(binding.edtEmail.editText.getText().toString().trim(),'@')<1)) {
                        binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                        false
                    }
                    else {
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
                            commaSeperatedString =
                                Utils.makeCommaSeperatedStringForPassword(
                                    Utils.removeAllCharacters(
                                        ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
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
                        }
                        else if(!(Utils.countOccurenceOfChar(binding.edtEmail.editText.getText().toString().trim(),'@')>0 && Utils.countOccurenceOfChar(binding.edtEmail.editText.getText().toString().trim(),'@')<2)){
                            binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                            false
                        }
                        else {
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

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = UserNameCheckReq(binding.edtEmail.getText().toString().trim())
        viewModelEmail.userNameAvailabilityCheck(request)


    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
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

                NewCreateAccountRequestModel.referenceId = resource.data?.referenceId
                findNavController().navigate(
                    R.id.action_forgotPasswordFragment_to_forgotOtpFragment,
                    bundle
                )
            }

            is Resource.DataError -> {
                binding.edtEmail.setErrorText(resource.errorMsg)


            }

            else -> {
            }
        }
    }


}