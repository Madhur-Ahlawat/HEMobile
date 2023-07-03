package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
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
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<ForgotpasswordChangesBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private var loader: LoaderDialog? = null
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var isCalled = false
    private lateinit var navFlow: String// create account , forgot password
    private var isViewCreated: Boolean = false
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var btnEnabled: Boolean = false


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ForgotpasswordChangesBinding.inflate(inflater, container, false)

    override fun init() {
        sessionManager.clearAll()
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()


        // binding.model = ConfirmOptionModel(identifier = "", enable = false)
        binding.email = ""
        binding.isValid = false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.edtEmail.editText.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)
        if (NewCreateAccountRequestModel.isEditCall) {
            navFlow = Constants.ACCOUNT_CREATION_EMAIL_FLOW
            NewCreateAccountRequestModel.emailAddress?.let { binding.edtEmail.setText(it) }
        }
        if (navFlow == Constants.ACCOUNT_CREATION_EMAIL_FLOW) {
            binding.textUsername.visible()
            binding.enterDetailsTxt.text = getString(R.string.createAccount_email_screenHeading)
            requireActivity().toolbar(getString(R.string.str_create_an_account))
        } else if (navFlow == Constants.FORGOT_PASSWORD_FLOW) {
            binding.enterDetailsTxt.text = getString(R.string.forgotPassword_email_screenHeading)
            requireActivity().toolbar(getString(R.string.forgot_password))
            binding.textUsername.gone()
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

    override fun initCtrl() {
        //binding.edtPostcode.addTextChangedListener { isEnable() }

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.confirmOption, ::handleConfirmOptionResponse)
        }
        if (!isViewCreated) {
            observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)

        }

        isViewCreated = true
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
                            bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
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


                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
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
                if (navFlow == Constants.FORGOT_PASSWORD_FLOW) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    sessionManager.saveAccountNumber(emailText)
                    isCalled = true
                    viewModel.confirmOptionForForgot(emailText)

                } else {
                    if (NewCreateAccountRequestModel.isEditCall && emailText == NewCreateAccountRequestModel.emailAddress) {
                        if (NewCreateAccountRequestModel.isAccountTypeEditCall) {
                            val bundle = Bundle()
                            bundle.putString(
                                Constants.NAV_FLOW_KEY,
                                Constants.ACCOUNT_CREATION_MOBILE_FLOW
                            )
                            findNavController().navigate(
                                R.id.action_forgotPasswordFragment_to_createPasswordFragment,
                                bundle
                            )
                        } else {
                            findNavController().popBackStack()
                        }
                    } else {
                        NewCreateAccountRequestModel.emailAddress = emailText
                        hitApi()
                    }

                }


            }
        }
    }


    private fun isEnable() {
        if (binding.edtEmail.getText().toString().trim().isEmpty()) {
            binding.edtEmail.removeError()
            btnEnabled = false
        } else {
            btnEnabled = if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString())
                    .matches()
            ) {
                binding.edtEmail.setErrorText(getString(R.string.str_email_format_error_message))
                false
            } else {
                if (binding.edtEmail.getText().toString().trim().length < 8) {
                    binding.edtEmail.setErrorText(getString(R.string.str_email_length_less_than_eight))
                    false
                } else {
                    binding.edtEmail.removeError()
                    true
                }

            }

        }
        checkButton()
    }

    private fun checkButton() {
        binding.btnNext.isEnabled = btnEnabled
    }

    private fun hitApi() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            Constants.EMAIL,
            binding.edtEmail.getText().toString().trim()
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


                bundle.putString(Constants.NAV_FLOW_KEY, navFlow)

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