package com.conduent.nationalhighways.ui.auth.forgot.password

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.VerifyRequestOtpResp
import com.conduent.nationalhighways.databinding.FragmentForgotCreateNewPasswordBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.hasLowerCase
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.common.Utils.hasUpperCase
import com.conduent.nationalhighways.utils.common.Utils.splCharsPassword
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewPasswordFragment : BaseFragment<FragmentForgotCreateNewPasswordBinding>(),
    View.OnClickListener {
    private var isNewPasswordValid: Boolean = false
    private var isConfirmPasswordValid: Boolean = false
    private val viewModel: ForgotPasswordViewModel by viewModels()

    private var data: SecurityCodeResponseModel? = null
    private var loader: LoaderDialog? = null
    private var passwordVisibile: Boolean = false
    private var confirmPasswordVisibile: Boolean = false
    private lateinit var navFlow: String


    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotCreateNewPasswordBinding =
        FragmentForgotCreateNewPasswordBinding.inflate(inflater, container, false)

    override fun init() {
        if(requireActivity() is AuthActivity){
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
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

        if (navFlow == Constants.ACCOUNT_CREATION_EMAIL_FLOW) {
//            binding.btnSubmit.text = getString(R.string.str_continue)
            /* AdobeAnalytics.setScreenTrack(
                 "createAccount:email_setPassword",
                 "set password",
                 "english",
                 "create Account",
                 "createAccount_email",
                 "login:forgot password:choose options:otp:new password set",
                 sessionManager.getLoggedInUser()
             )*/
        } else if (navFlow == Constants.FORGOT_PASSWORD_FLOW) {
//            binding.btnSubmit.text = getString(R.string.str_submit)

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
        binding.edtNewPassword.editText.addTextChangedListener {
            isEnable(it.toString())
        }
        binding.edtConformPassword.editText.addTextChangedListener { isEnable1(it.toString()) }



        binding.edtNewPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtNewPassword.right - binding.edtNewPassword.editText.compoundDrawables[right].bounds.width()) {

                    if (passwordVisibile) {
                        binding.edtNewPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtNewPassword.editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisibile = false
                    } else {


                        binding.edtNewPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtNewPassword.editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        passwordVisibile = true
                    }
                }
                var text = binding.edtConformPassword.editText.text.toString()
                isEnable(text)
            }

            false
        }


        binding.edtConformPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtConformPassword.right - binding.edtConformPassword.editText.compoundDrawables[right].bounds.width()) {

                    if (confirmPasswordVisibile) {
                        binding.edtConformPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtConformPassword.editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        confirmPasswordVisibile = false
                    } else {

                        binding.edtConformPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtConformPassword.editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        confirmPasswordVisibile = true
                    }
                }
                var text = binding.edtConformPassword.editText.text.toString()
                isEnable1(text)
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
                if (navFlow != Constants.FORGOT_PASSWORD_FLOW) {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                    emailHeartBeatApi()
                    NewCreateAccountRequestModel.password =
                        binding.edtNewPassword.getText().toString().trim()
                    findNavController().navigate(
                        R.id.action_createPasswordFragment_to_optForSmsFragment,
                        bundle
                    )
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
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
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
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlow)
                    bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                    if (navFlow == Constants.FORGOT_PASSWORD_FLOW) {
                        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                        findNavController().navigate(
                            R.id.action_createPasswordFragment_to_resetFragment,
                            bundle
                        )

                    } else {
                        findNavController().navigate(
                            R.id.action_createPasswordFragment_to_optForSmsFragment,
                            bundle
                        )
                    }
                } else
                    showError(binding.root, status.data?.message)
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
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
            }

            else -> {
            }
        }
    }

    private fun isEnable1(text: String) {
        isConfirmPasswordValid = true

        if (binding.edtConformPassword.getText().toString().length == 0) {
            isConfirmPasswordValid = false
            binding.edtConformPassword.removeError()
        } else if (binding.edtConformPassword.getText().toString()
                .isNotEmpty() && binding.edtNewPassword.getText()
                .toString() != binding.edtConformPassword.getText().toString()
        ) {
            isConfirmPasswordValid = false
            binding.edtConformPassword.setErrorText(getString(R.string.str_your_password_must_match))

        } else {
//            isConfirmPasswordValid = true
//            isNewPasswordValid = true
            binding.edtConformPassword.removeError()
//            binding.edtNewPassword.removeError()
            binding.edtNewPassword.editText.setText(binding.edtNewPassword.editText.text.toString())
//            binding.model = ResetPasswordModel(
//                code = data?.code,
//                referenceId = data?.referenceId,
//                newPassword = binding.edtNewPassword.getText().toString(),
//                confirmPassword = binding.edtConformPassword.getText().toString(),
//                enable = true
//            )
        }
        binding.btnSubmit.isEnabled = isNewPasswordValid && isConfirmPasswordValid
    }

    private fun isEnable(text: String) {
        var filterTextForSpecialChars = ""
        var commaSeperatedString = ""
        isNewPasswordValid = true

        if (binding.edtNewPassword.getText().toString().isEmpty()) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
        } else if (hasSpecialCharacters(
                binding.edtNewPassword.editText.text.toString(),
                splCharsPassword
            ) || binding.edtNewPassword.getText().toString().contains(" ")
        ) {
            isNewPasswordValid = false

            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                Utils.LOWER_CASE,
                binding.edtNewPassword.getText().toString()
            )
            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                Utils.UPPER_CASE,
                binding.edtNewPassword.getText().toString()
            )
            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                Utils.DIGITS,
                binding.edtNewPassword.getText().toString()
            )
            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
                Utils.ALLOWED_CHARS_EMAIL,
                binding.edtNewPassword.getText().toString()
            )
            filterTextForSpecialChars.replace(" ", "space ")
            commaSeperatedString =
                Utils.makeCommaSeperatedStringForPassword(
                    Utils.removeAllCharacters(
                        Utils.ALLOWED_CHARS_PASSWORD, filterTextForSpecialChars
                    )
                )
            if (filterTextForSpecialChars.isNotEmpty()) {
                binding.edtNewPassword.setErrorText("Password must not include $commaSeperatedString")
                false
            } else {
                binding.edtNewPassword.removeError()
                true
            }
        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.NUMBER)) {
            isNewPasswordValid = false

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_contain_at_least_one_character))

        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.UPPERCASE)) {
            isNewPasswordValid = false

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_upper_case))

        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.LOWECASE)) {
            isNewPasswordValid = false

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_lower_case))
        } else if (binding.edtNewPassword.getText().toString().length < 8) {
            isNewPasswordValid = false

            binding.edtNewPassword.setErrorText(getString(R.string.password_must_be_8_characters))

        } else if (binding.edtConformPassword.getText().toString()
                .isNotEmpty() && binding.edtNewPassword.getText().toString()
            != binding.edtConformPassword.getText().toString()
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
            binding.edtConformPassword.setErrorText(getString(R.string.str_your_password_must_match))
        } else {
            isNewPasswordValid = true

            binding.edtNewPassword.removeError()

            if (binding.edtConformPassword.getText().toString().isEmpty()) {
                isConfirmPasswordValid = false
                binding.edtConformPassword.removeError()
            } else {

                binding.model = ResetPasswordModel(
                    code = data?.code,
                    referenceId = data?.referenceId,
                    newPassword = binding.edtNewPassword.getText().toString(),
                    confirmPassword = binding.edtConformPassword.getText().toString(),
                    enable = true
                )

                isConfirmPasswordValid = true
                binding.edtConformPassword.removeError()
            }
        }

        if (hasLowerCase(text)) {
            setTickBackground(binding.imgDot3)
        } else {
            setDotBackground(binding.imgDot3)
        }

        if (hasUpperCase(text)) {
            setTickBackground(binding.imgDot2)
        } else {
            setDotBackground(binding.imgDot2)
        }

        if (text.length >= 8) {
            setTickBackground(binding.imgDot1)
        } else {
            setDotBackground(binding.imgDot1)
        }

        if (hasDigits(text)) {
            setTickBackground(binding.imgDot4)
        } else {
            setDotBackground(binding.imgDot4)
        }
//new password

        binding.btnSubmit.isEnabled = isNewPasswordValid && isConfirmPasswordValid
    }

    fun setDotBackground(view: View) {
        view.contentDescription=resources.getString(R.string.accessibility_bullet)
        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.circle_5dp)
    }

    fun setTickBackground(view: View) {
        view.contentDescription=resources.getString(R.string.accessibility_check_mark)
        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.grin_tick)
    }

    private fun heartBeatApiResponse(resource: Resource<EmptyApiResponse?>?) {

    }

    override fun onResume() {
        super.onResume()
    }


}

