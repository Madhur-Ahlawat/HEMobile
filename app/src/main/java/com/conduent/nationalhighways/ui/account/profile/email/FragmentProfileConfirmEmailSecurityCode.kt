package com.conduent.nationalhighways.ui.account.profile.email

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.model.profile.ProfileUpdateEmailModel
import com.conduent.nationalhighways.databinding.FragmentProfileConfirmEmailSecurityCodeBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProfileConfirmEmailSecurityCode : BaseFragment<FragmentProfileConfirmEmailSecurityCodeBinding>(),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var response: SecurityCodeResponseModel? = null
    var data:ProfileUpdateEmailModel?=null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileConfirmEmailSecurityCodeBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments != null) {
            data = arguments?.getParcelable("data")
            response = arguments?.getParcelable("response")
            binding.data = data
        }
        binding.tvMsg.text = getString(R.string.send_security_code_msg, binding.data?.emailAddress)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)
        }
    }

    override fun initCtrl() {
        binding.apply {
            enable = false
            etCode.onTextChanged {
                enable = etCode.text.toString().trim().isNotEmpty() && etCode.text.toString()
                    .trim().length > 5
            }
//            etCode.editText.addTextChangedListener {
//
//                verifyCodeErrorMessage()
//
//
//            }
            btnAction.setOnClickListener(this@FragmentProfileConfirmEmailSecurityCode)
            tvResend.setOnClickListener(this@FragmentProfileConfirmEmailSecurityCode)
        }
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
//        observe(viewModel.emailValidation, ::handleEmailValidation)
        observe(viewModel.verifyRequestCode, ::verifyRequestOtp)

    }
    private var accountInformation: AccountInformation? = null

//    private fun verifyRequestOtp(status: Resource<VerifyRequestOtpResp?>?) {
//        if (loader?.isVisible == true) {
//            loader?.dismiss()
//        }
//        when (status) {
//            is Resource.Success -> {
//                val bundle = Bundle()
//
//                if (navFlowCall == Constants.TWOFA) {
//                    if (accountInformation?.status.equals(Constants.SUSPENDED)) {
//                        bundle.putParcelable(Constants.PERSONALDATA, HomeActivityMain.accountDetailsData.personalInformation)
//
//                        bundle.putString(
//                            Constants.CURRENTBALANCE, replenishmentInformation?.currentBalance
//                        )
//                    } else {
//                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)
//
//                    }
//
//
//                } else {
//                    response?.code = binding.edtOtp.getText().toString()
//                    bundle.putParcelable("data", response)
//                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
//
//                    Logg.logging("NewPassword", "response $response")
//                    AdobeAnalytics.setActionTrack(
//                        "verify",
//                        "login:forgot password:choose options:otp",
//                        "forgot password",
//                        "english",
//                        "login",
//                        (requireActivity() as AuthActivity).previousScreen,
//                        sessionManager.getLoggedInUser()
//                    )
//
//
//                    findNavController().navigate(
//                        R.id.action_otpFragment_to_createPasswordFragment,
//                        bundle
//                    )
//                }
//
//
//
//                AdobeAnalytics.setActionTrack1(
//                    "verify",
//                    "login:forgot password:choose options:otp:new password set",
//                    "forgot password",
//                    "english",
//                    "login",
//                    (requireActivity() as AuthActivity).previousScreen, "success",
//                    sessionManager.getLoggedInUser()
//                )
//
//            }
//
//            is Resource.DataError -> {
//                Logg.logging("NewPassword", "status.errorMsg ${status.errorMsg}")
//
//                AdobeAnalytics.setActionTrack1(
//                    "verify",
//                    "login:forgot password:choose options:otp:new password set",
//                    "forgot password",
//                    "english",
//                    "login",
//                    (requireActivity() as AuthActivity).previousScreen,
//                    status.errorMsg,
//                    sessionManager.getLoggedInUser()
//                )
//
//
//            }
//
//            else -> {
//            }
//        }
//    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.emailValidationForUpdatation(binding.data)
            }

            R.id.tvResend -> {
                binding.data?.referenceId = ""
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
//                val request = EmailVerificationRequest(
//                    Constants.EMAIL_SELECTION_TYPE,
//                    binding.data?.emailAddress ?: ""
//                )
//                viewModel.emailVerificationApi(request)

                val mVerifyRequestOtpReq =
                    VerifyRequestOtpReq(
                        binding.etCode.getText().toString(),
                        response?.referenceId
                    )
                viewModel.verifyRequestCode(mVerifyRequestOtpReq)
            }
        }
    }

}
