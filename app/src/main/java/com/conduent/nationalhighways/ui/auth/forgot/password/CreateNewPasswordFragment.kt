package com.conduent.nationalhighways.ui.auth.forgot.password

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.*
import com.conduent.nationalhighways.databinding.FragmentForgotCreateNewPasswordBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
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
    private var passwordVisibile: Boolean = false
    private var confirmPasswordVisibile: Boolean = false
    private lateinit var  navFlow:String


    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotCreateNewPasswordBinding =
        FragmentForgotCreateNewPasswordBinding.inflate(inflater, container, false)

    override fun init() {
        navFlow = arguments?.getString(Constants.NAV_FLOW_KEY).toString()

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

        if (navFlow==Constants.ACCOUNT_CREATION_EMAIL_FLOW){
            binding.btnSubmit.text=getString(R.string.str_next)
           /* AdobeAnalytics.setScreenTrack(
                "createAccount:email_setPassword",
                "set password",
                "english",
                "create Account",
                "createAccount_email",
                "login:forgot password:choose options:otp:new password set",
                sessionManager.getLoggedInUser()
            )*/
        }else if(navFlow==Constants.FORGOT_PASSWORD_FLOW){
            binding.btnSubmit.text=getString(R.string.str_submit)

           /* AdobeAnalytics.setScreenTrack(
                "login:forgot password:choose options:otp:new password set",
                "forgot password",
                "english",
                "login",
                (requireActivity() as AuthActivity).previousScreen,
                "login:forgot password:choose options:otp:new password set",
                sessionManager.getLoggedInUser()
            )*/
        }


     //  viewModel.verifyRequestCode(mVerifyRequestOtpReq)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initCtrl() {
        binding.btnSubmit.setOnClickListener(this)
        binding.edtNewPassword.addTextChangedListener {
            isEnable(it.toString()) }
        binding.edtConformPassword.addTextChangedListener { isEnable1() }

        binding.text1.text=getString(R.string.dotunicode)+" "+getString(R.string.str_at_least_8_character)
        binding.text2.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_upper_case)
        binding.text3.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_lower_case)
        binding.text4.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_number)


        binding.edtNewPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtNewPassword.right - binding.edtNewPassword.compoundDrawables[right].bounds.width()) {

                    if (passwordVisibile) {
                        binding.edtNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtNewPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisibile = false
                    } else {


                        binding.edtNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtNewPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        passwordVisibile = true
                    }
                }
            }

            false
        }


        binding.edtConformPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtConformPassword.right - binding.edtConformPassword.compoundDrawables[right].bounds.width()) {

                    if (confirmPasswordVisibile) {
                        binding.edtConformPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtConformPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        confirmPasswordVisibile = false
                    } else {

                        binding.edtConformPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtConformPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        confirmPasswordVisibile = true
                    }
                }
            }

            false
        }


    }

    override fun observer() {
        observe(viewModel.resetPassword, ::handleResetResponse)
        //observe(viewModel.verifyRequestCode, ::verifyRequestOtp)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                hideKeyboard()
                if (navFlow != Constants.FORGOT_PASSWORD_FLOW){
                    val bundle=Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                    findNavController().navigate(R.id.action_createPasswordFragment_to_optForSmsFragment,bundle)
                return
                }
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
               /* AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen, "success",
                    sessionManager.getLoggedInUser()
                )*/

            }
            is Resource.DataError -> {
                Logg.logging("NewPassword", "status.errorMsg ${status.errorMsg}")

               /* AdobeAnalytics.setActionTrack1(
                    "verify",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    status.errorMsg,
                    sessionManager.getLoggedInUser()
                )
*/

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
                   /* AdobeAnalytics.setActionTrack1(
                        "submit",
                        "login:forgot password:choose options:otp:new password set",
                        "forgot password",
                        "english",
                        "login",
                        (requireActivity() as AuthActivity).previousScreen,
                        "success",
                        sessionManager.getLoggedInUser()
                    )*/
                    val bundle=Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                    if (navFlow==Constants.FORGOT_PASSWORD_FLOW){
                        findNavController().navigate(R.id.action_createPasswordFragment_to_resetFragment,bundle)

                    }else{
                        findNavController().navigate(R.id.action_createPasswordFragment_to_optForSmsFragment,bundle)
                    }
                } else
                    showError(binding.root, status.data?.message)
            }
            is Resource.DataError -> {
                /*AdobeAnalytics.setActionTrack1(
                    "submit",
                    "login:forgot password:choose options:otp:new password set",
                    "forgot password",
                    "english",
                    "login",
                    (requireActivity() as AuthActivity).previousScreen,
                    status.errorMsg,
                    sessionManager.getLoggedInUser()
                )*/

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
            binding.text1.text=getString(R.string.tickunicode)+" "+getString(R.string.str_at_least_8_character)
        }else{
            binding.text1.text=getString(R.string.dotunicode)+" "+getString(R.string.str_at_least_8_character)
        }

        if (Utils.validateString(text,Utils.PASSWORD_RULE2)){
            binding.text2.text=getString(R.string.tickunicode)+" "+getString(R.string.str_contain_at_least_one_upper_case)
            binding.text3.text=getString(R.string.tickunicode)+" "+getString(R.string.str_contain_at_least_one_lower_case)


        }else{
            binding.text2.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_upper_case)
            binding.text3.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_lower_case)

        }

        if (Utils.validateString(text,Utils.PASSWORD_RULE3)){
            binding.text4.text=getString(R.string.tickunicode)+" "+getString(R.string.str_contain_at_least_one_number)
        }else{
            binding.text4.text=getString(R.string.dotunicode)+" "+getString(R.string.str_contain_at_least_one_number)
        }
    }

}