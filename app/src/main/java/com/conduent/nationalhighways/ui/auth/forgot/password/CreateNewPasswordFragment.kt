package com.conduent.nationalhighways.ui.auth.forgot.password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.*
import com.conduent.nationalhighways.databinding.FragmentForgotCreateNewPasswordBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.jce.provider.BrokenPBE.Util
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewPasswordFragment : BaseFragment<FragmentForgotCreateNewPasswordBinding>(),
    View.OnClickListener {
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var data: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotCreateNewPasswordBinding =
        FragmentForgotCreateNewPasswordBinding.inflate(inflater, container, false)

    override fun init() {

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        data = arguments?.getParcelable("data")
        binding.model = ResetPasswordModel(
            code = data?.code,
            referenceId = data?.referenceId,
            newPassword = "",
            confirmPassword = "",
            enable = false
        )
//        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val mVerifyRequestOtpReq =
            VerifyRequestOtpReq(data?.code, data?.referenceId)
        Logg.logging("NewPassword", "mVerifyRequestOtpReq $mVerifyRequestOtpReq")
        AdobeAnalytics.setScreenTrack(
            "login:forgot password:choose options:otp:new password set",
            "forgot password",
            "english",
            "login",
            (requireActivity() as AuthActivity).previousScreen,
            "login:forgot password:choose options:otp:new password set",
            sessionManager.getLoggedInUser()
        )

     //  viewModel.verifyRequestCode(mVerifyRequestOtpReq)
    }

    override fun initCtrl() {
        binding.btnSubmit.setOnClickListener(this)
        binding.edtNewPassword.addTextChangedListener {
            isEnable(it.toString()) }
        binding.edtConformPassword.addTextChangedListener { isEnable1() }
    }

    override fun observer() {
        observe(viewModel.resetPassword, ::handleResetResponse)
        observe(viewModel.verifyRequestCode, ::verifyRequestOtp)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                hideKeyboard()
                val validation = viewModel.checkPassword(binding.model)
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    Logg.logging("NewPassword", "binding.model ${binding.model}")
                    viewModel.resetPassword(binding.model)
                } else {
                    showError(binding.root, validation.second)
                }
            }
        }
    }


    private fun verifyRequestOtp(status: Resource<VerifyRequestOtpResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
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

    private fun handleResetResponse(status: Resource<ForgotPasswordResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.success == true) {
                    AdobeAnalytics.setActionTrack1(
                        "submit",
                        "login:forgot password:choose options:otp:new password set",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen,
                        "success",
                        sessionManager.getLoggedInUser()
                    )

                    findNavController().navigate(R.id.action_createPasswordFragment_to_resetFragment)
                } else
                    showError(binding.root, status.data?.message)
            }
            is Resource.DataError -> {
                AdobeAnalytics.setActionTrack1(
                    "submit",
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

    private fun isEnable1() {
        if (binding.model?.newPassword?.isEmpty() == false
            && binding.model?.confirmPassword?.isEmpty() == false
            && ((binding.model?.newPassword?.length ?: 0) > 7)
            && ((binding.model?.confirmPassword?.length ?: 0) > 7&&Utils.isValidPassword(binding.model?.newPassword)&&Utils.isValidPassword(binding.model?.confirmPassword))
        ) {
            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.text.toString(),
                confirmPassword = binding.edtConformPassword.text.toString(),
                enable = true
            )

        } else {
            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.text.toString(),
                confirmPassword = binding.edtConformPassword.text.toString(),
                enable = false
            )

        }


    }

    private fun isEnable(text: String) {
        if (binding.model?.newPassword?.isEmpty() == false
            && binding.model?.confirmPassword?.isEmpty() == false
            && ((binding.model?.newPassword?.length ?: 0) > 7)
            && ((binding.model?.confirmPassword?.length ?: 0) > 7&&Utils.isValidPassword(binding.model?.newPassword)&&Utils.isValidPassword(binding.model?.confirmPassword))
        ) {
            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.text.toString(),
                confirmPassword = binding.edtConformPassword.text.toString(),
                enable = true
            )

        } else {
            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.text.toString(),
                confirmPassword = binding.edtConformPassword.text.toString(),
                enable = false
            )

        }

        if (Utils.validateString(text,Utils.PASSWORD_RULE1)){
            binding.imgDot1.setImageResource(R.drawable.ic_tick)
        }else{
            binding.imgDot1.setImageResource(R.drawable.white_circular)
        }

        if (Utils.validateString(text,Utils.PASSWORD_RULE2)){
            binding.imgDot2.setImageResource(R.drawable.ic_tick)
            binding.imgDot3.setImageResource(R.drawable.ic_tick)

        }else{
            binding.imgDot2.setImageResource(R.drawable.white_circular)
            binding.imgDot3.setImageResource(R.drawable.white_circular)

        }

        if (Utils.validateString(text,Utils.PASSWORD_RULE3)){
            binding.imgDot4.setImageResource(R.drawable.ic_tick)
        }else{
            binding.imgDot4.setImageResource(R.drawable.ic_tick)
        }
    }

}