package com.conduent.nationalhighways.ui.auth.forgot.password

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.databinding.FragmentForgotOtpBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class OTPForgotPassword : BaseFragment<FragmentForgotOtpBinding>(), View.OnClickListener {


    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var data: RequestOTPModel? = null
    private var response: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null
    private var timer: CountDownTimer? = null
    private var timeFinish: Boolean = false
    private var isCalled = true
    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotOtpBinding = FragmentForgotOtpBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

        data = arguments?.getParcelable("data")
        binding.isEnable = false
        loadUI()
        viewModel.requestOTP(data)

        AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:otp",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:otp",
            sessionManager.getLoggedInUser()
        )

    }

    override fun initCtrl() {
        binding.apply {
            btnVerify.setOnClickListener(this@OTPForgotPassword)
            resendTxt.setOnClickListener(this@OTPForgotPassword)
            edtOtp.addTextChangedListener { binding.isEnable = (it?.length ?: 0) > 5 }
        }
    }

    override fun observer() {
        observe(viewModel.otp, ::handleOTPResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify -> {

                if (!timeFinish) {
                    val bundle = Bundle()
                    response?.code = binding.edtOtp.text.toString()
                    bundle.putParcelable("data", response)
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
                } else {
                    showError(binding.root, getString(R.string.error_otp_time_expire))
                }
            }

            R.id.resend_txt -> {
                AdobeAnalytics.setActionTrack(
                    "resend",
                    "login:forgot password:choose options:otp",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    sessionManager.getLoggedInUser()
                )

                isCalled = true
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.requestOTP(data)
            }
        }
    }

    private fun loadUI() {
        when (data?.optionType) {
            Constants.SMS -> {
                binding.topTitle.text = getString(R.string.str_check_sms)
                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_sms)
            }
            Constants.EMAIL -> {
                binding.topTitle.text = getString(R.string.str_check_your_mail)
                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_txt)
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

}