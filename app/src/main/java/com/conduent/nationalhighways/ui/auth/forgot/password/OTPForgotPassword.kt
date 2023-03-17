package com.conduent.nationalhighways.ui.auth.forgot.password

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpResp
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.databinding.FragmentForgotOtpBinding
import com.conduent.nationalhighways.databinding.FragmentForgotOtpchangesBinding
import com.conduent.nationalhighways.ui.account.creation.step1.CreateAccountEmailViewModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class OTPForgotPassword : BaseFragment<FragmentForgotOtpchangesBinding>(), View.OnClickListener {


    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var data: RequestOTPModel? = null
    private var response: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null
    private var timer: CountDownTimer? = null
    private var timeFinish: Boolean = false
    private var isCalled = true
    @Inject
    lateinit var sessionManager: SessionManager
    private var isViewCreated:Boolean=false
    private lateinit var  navFlow:String
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()




    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotOtpchangesBinding = FragmentForgotOtpchangesBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

        if (arguments!=null){
            data = arguments?.getParcelable("data")
            response=arguments?.getParcelable("response")
        }

        binding.isEnable = false
        loadUI()


        /*AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:otp",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:otp",
            sessionManager.getLoggedInUser()
        )*/



    }

    override fun initCtrl() {
        binding.apply {
            btnVerify.setOnClickListener(this@OTPForgotPassword)
            btnResend.setOnClickListener(this@OTPForgotPassword)
            edtOtp.addTextChangedListener { binding.isEnable = (it?.length ?: 0) > 5 }
        }
    }

    override fun observer() {
        if (!isViewCreated){
            observe(viewModel.otp, ::handleOTPResponse)
            observe(viewModel.verifyRequestCode, ::verifyRequestOtp)
            observe(createAccountViewModel.confirmEmailApiVal, ::handleConfirmEmailResponse)

        }

        isViewCreated=true

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify -> {

                if (navFlow==Constants.FORGOT_PASSWORD_FLOW){
                    if (!timeFinish) {
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                        val mVerifyRequestOtpReq =
                            VerifyRequestOtpReq(binding.edtOtp.text.toString(), response?.referenceId)
                        viewModel.verifyRequestCode(mVerifyRequestOtpReq)

                    } else {
                        showError(binding.root, getString(R.string.error_otp_time_expire))
                    }
                }else{
                    confirmEmailCode()
                }

            }

            R.id.btn_Resend -> {
                AdobeAnalytics.setActionTrack(
                    "resend",
                    "login:forgot password:choose options:otp",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    sessionManager.getLoggedInUser()
                )
                val bundle=Bundle()
                bundle.putParcelable("data",data)
                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                findNavController().navigate(R.id.action_otpFragment_to_resenedCodeFragment,bundle)

               /* isCalled = true
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.requestOTP(data)*/
            }
        }
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = ConfirmEmailRequest(
            response?.referenceId?.toString() ?: "",
            data?.optionValue,
            binding.edtOtp.text.toString().trim()
        )
        createAccountViewModel.confirmEmailApi(request)
    }
    private fun loadUI() {
        when (data?.optionType) {
            Constants.SMS -> {
                binding.topTitle.text = getString(R.string.str_check_sms)
                binding.messageReceivedTxt.text=getString(R.string.wehavesentatextmessageto)+" "+ data!!.optionValue

                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_sms)
            }
            Constants.EMAIL -> {
                binding.topTitle.text = getString(R.string.str_check_your_mail)
                binding.messageReceivedTxt.text=getString(R.string.wehavesentanemail)+" "+ data!!.optionValue
                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_txt)
            }
        }
    }

    private fun verifyRequestOtp(status: Resource<VerifyRequestOtpResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                val bundle = Bundle()
                response?.code = binding.edtOtp.text.toString()
                bundle.putParcelable("data", response)
                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)

                Logg.logging("NewPassword","response $response")
                AdobeAnalytics.setActionTrack(
                    "verify",
                    "login:forgot password:choose options:otp",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    sessionManager.getLoggedInUser()
                )

                findNavController().navigate(
                    R.id.action_otpFragment_to_createPasswordFragment,
                    bundle
                )

                AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, "success",
                    sessionManager.getLoggedInUser()
                )

            }
            is Resource.DataError -> {
                Logg.logging("NewPassword", "status.errorMsg ${status.errorMsg}")

                AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    status.errorMsg,
                    sessionManager.getLoggedInUser()
                )


                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {
        Logg.logging("NewPassword","response handleOTPResponse called  $response")

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    response = status.data
                    Logg.logging("NewPassword","response api call  $response")

                    timer = object : CountDownTimer(response?.otpExpiryInSeconds ?: 0L, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            timeFinish = true
                        }

                        override fun onFinish() {
                            timeFinish = true
                        }
                    }

                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }
            isCalled = false
        }
    }

    private fun handleConfirmEmailResponse(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

            when (resource) {
                is Resource.Success -> {


                    val bundle = Bundle()
                    if (navFlow==Constants.ACCOUNT_CREATION_MOBILE_FLOW){
                        Toast.makeText(requireContext(),"Navigate to Add Vehicle Screen ",Toast.LENGTH_LONG).show()

                    }else{
                        response?.code = binding.edtOtp.text.toString()
                        bundle.putParcelable("data", response)
                        bundle.putString(Constants.NAV_FLOW_KEY,navFlow)

                        findNavController().navigate(
                            R.id.action_forgotOtpFragment_to_createPasswordFragment,
                            bundle
                        )
                    }



                }
                is Resource.DataError -> {

                        showError(binding.root, resource.errorMsg)

                }
                else -> {
                }
            }

    }


}