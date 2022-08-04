package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.databinding.FragmentForgotOtpBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import kotlin.getValue


@AndroidEntryPoint
class OTPForgotPassword : BaseFragment<FragmentForgotOtpBinding>(), View.OnClickListener {
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var data: RequestOTPModel? = null
    private var response: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null
    private var timer: CountDownTimer? = null
    private var timeFinish: Boolean = false


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
//                if(response?.code?.equals(binding.edtOtp.text.toString())==true) {
                if (!timeFinish) {
                    val bundle = Bundle()
                    response?.code = binding.edtOtp.text.toString()
                    bundle.putParcelable("data", response)
                    findNavController().navigate(
                        R.id.action_otpFragment_to_createPasswordFragment,
                        bundle
                    )
                } else {
                    showError(binding.root, getString(R.string.error_otp_time_expire))
                }
//                }
//                else { showError(binding.root,getString(R.string.enter_otp)) }

            }

            R.id.resend_txt -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.requestOTP(data)
            }
        }
    }

    private fun loadUI() {
        when (data?.optionType) {

            Constants.SMS -> {
                binding.topTitle.text = getString(R.string.check_sms)
                binding.notReceivedTxt.text = getString(R.string.str_not_sms_received_otp_txt)
            }
            Constants.EMAIL -> {
                binding.topTitle.text = getString(R.string.str_check_your_mail)
                binding.notReceivedTxt.text = getString(R.string.str_not_received_otp_txt)
            }
        }
    }

    private fun handleOTPResponse(status: Resource<SecurityCodeResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                response = status.data

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
    }


}