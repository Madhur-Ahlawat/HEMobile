package com.conduent.nationalhighways.ui.account.profile.password

import android.annotation.SuppressLint
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.databinding.FragmentChangePasswordProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.hasDigits
import com.conduent.nationalhighways.utils.common.Utils.hasLowerCase
import com.conduent.nationalhighways.utils.common.Utils.hasUpperCase
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordProfileFragment : BaseFragment<FragmentChangePasswordProfileBinding>(),
    View.OnClickListener {
    private var isNewPasswordValid: Boolean = false
    private var isConfirmPasswordValid: Boolean = false
    private val viewModel: ProfileViewModel by viewModels()
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
    ): FragmentChangePasswordProfileBinding =
        FragmentChangePasswordProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        data = arguments?.getParcelable("data")

        //  viewModel.verifyRequestCode(mVerifyRequestOtpReq)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initCtrl() {
        binding.btnSubmit.setOnClickListener(this)
        binding.edtNewPassword.editText.addTextChangedListener {
            isEnable(it.toString())
        }
        binding.edtConfirmPassword.editText.addTextChangedListener { isEnable1(it.toString()) }



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
            }

            false
        }


        binding.edtConfirmPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtConfirmPassword.right - binding.edtConfirmPassword.editText.compoundDrawables[right].bounds.width()) {

                    if (confirmPasswordVisibile) {
                        binding.edtConfirmPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtConfirmPassword.editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        confirmPasswordVisibile = false
                    } else {

                        binding.edtConfirmPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtConfirmPassword.editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        confirmPasswordVisibile = true
                    }
                }
            }

            false
        }


    }

    override fun observer() {
        observe(viewModel.updatePassword, ::handleResetResponse)
        //observe(viewModel.verifyRequestCode, ::verifyRequestOtp)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                hideKeyboard()
                val validation = viewModel.checkPassword(
                    newPassword = binding.edtNewPassword.editText.getText().toString().trim(),
                    currentPassword = binding.edtCurrentPassword.editText.getText().toString().trim(),
                    confirmPassword = binding.edtConfirmPassword.editText.getText().toString().trim()
                )
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    disableButton()
                    viewModel.updatePassword(
                        ResetPasswordModel(
                            currentPassword = binding.edtCurrentPassword.editText.text.toString(),
                            newPassword = binding.edtNewPassword.editText.text.toString(),
                            confirmPassword = binding.edtConfirmPassword.editText.text.toString()
                        )
                    )
                } else {
                    showError(binding.root, validation.second)
                }
            }
        }
    }

    fun enableButton() {
        binding.btnSubmit.isEnabled = true
        binding.btnSubmit.isFocusable = true
    }

    fun disableButton() {
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.isFocusable = false
    }

    private fun handleResetResponse(status: Resource<ForgotPasswordResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode != "1308" && status.data?.message!!.lowercase(Locale.ROOT)
                        .contains("success")
                ) {
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
                    sessionManager.clearAll()
                    Intent(
                        requireActivity(),
                        ProfilePasswordChangeSuccessActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                    requireActivity().finish()

                } else {
                    enableButton()
                    showError(binding.root, status.data?.message)
                }
            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(Constants.INVALID_TOKEN))|| status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR ) {
                    displaySessionExpireDialog(status.errorModel)
                }else {
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
        var filterTextForSpecialChars = ""
        var commaSeperatedString = ""
        isConfirmPasswordValid = true

        if (binding.edtConfirmPassword.getText().toString().length == 0) {
            isConfirmPasswordValid = false
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_confirm_password_error_message))
        } else if (binding.edtNewPassword.getText()
                .toString() != binding.edtConfirmPassword.getText().toString()
        ) {
            isConfirmPasswordValid = false
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_your_password_must_match))

        }
//        else if (hasSpecialCharacters(
//                binding.edtConfirmPassword.editText.getText().toString(),
//                Utils.splCharsPassword
//            ) || binding.edtConfirmPassword.getText().toString().contains(" ")
//        ) {
//            isConfirmPasswordValid = false
//            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
//                Utils.LOWER_CASE,
//                binding.edtConfirmPassword.getText().toString()
//            )
//            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
//                Utils.UPPER_CASE,
//                binding.edtConfirmPassword.getText().toString()
//            )
//            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
//                Utils.DIGITS,
//                binding.edtConfirmPassword.getText().toString()
//            )
//            filterTextForSpecialChars = Utils.removeGivenStringCharactersFromString(
//                Utils.ALLOWED_CHARS_EMAIL,
//                binding.edtConfirmPassword.getText().toString()
//            )
//            filterTextForSpecialChars.replace(" ","space ")
//
//            commaSeperatedString =
//                Utils.makeCommaSeperatedStringForPassword(
//                    Utils.removeAllCharacters(
//                        Utils.ALLOWED_CHARS_PASSWORD, filterTextForSpecialChars!!
//                    )
//                )
//            if (filterTextForSpecialChars!!.length > 0) {
//                binding.edtConfirmPassword.setErrorText("Password must not include $commaSeperatedString")
//            } else {
//                binding.edtConfirmPassword.removeError()
//            }
//        }
//        else if (!binding.edtConfirmPassword.getText().toString().contains(Utils.NUMBER)) {
//            isConfirmPasswordValid = false
//
//            binding.edtConfirmPassword.setErrorText(getString(R.string.str_password_must_contain_at_least_one_character))
//
//        } else if (!binding.edtConfirmPassword.getText().toString()
//                .contains(Utils.UPPERCASE)
//        ) {
//            isConfirmPasswordValid = false
//
//            binding.edtConfirmPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_upper_case))
//
//        } else if (!binding.edtConfirmPassword.getText().toString()
//                .contains(Utils.LOWECASE)
//        ) {
//            isConfirmPasswordValid = false
//
//            binding.edtConfirmPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_lower_case))
//
//        } else if (binding.edtConfirmPassword.getText().toString().length < 8) {
//            isConfirmPasswordValid = false
//
//            binding.edtConfirmPassword.setErrorText(getString(R.string.password_must_be_8_characters))
//
//        } else if (binding.edtNewPassword.getText().toString()
//            != binding.edtConfirmPassword.getText().toString()
//        ) {
//            isConfirmPasswordValid = false
//
//            binding.edtConfirmPassword.setErrorText(getString(R.string.str_your_password_must_match))
//        }
        else {
            isConfirmPasswordValid = true
            isNewPasswordValid = true
            binding.edtConfirmPassword.removeError()
            binding.edtNewPassword.removeError()

            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.getText().toString(),
                confirmPassword = binding.edtConfirmPassword.getText().toString(),
                enable = true
            )
        }

//        if (hasLowerCase(text)) {
//            binding.imgDot3.setImageResource(R.drawable.grin_tick)
//        }
//        else {
//            binding.imgDot3.setImageResource(R.drawable.circle_5dp)
//        }
//
//        if (hasUpperCase(text)) {
//            binding.imgDot2.setImageResource(R.drawable.grin_tick)
//        }
//        else {
//            binding.imgDot2.setImageResource(R.drawable.circle_5dp)
//        }
//
//        if (text.length >= 8) {
//            binding.imgDot1.setImageResource(R.drawable.grin_tick)
//        }
//        else {
//            binding.imgDot1.setImageResource(R.drawable.circle_5dp)
//        }
//
//        if (hasDigits(text)) {
//            binding.imgDot4.setImageResource(R.drawable.grin_tick)
//        }
//        else {
//            binding.imgDot4.setImageResource(R.drawable.circle_5dp)
//        }
//
        if (isNewPasswordValid && isConfirmPasswordValid) {
            binding.btnSubmit.isEnabled = true
        } else {
            binding.btnSubmit.isEnabled = false
        }
    }

    private fun isEnable(text: String) {
        var filterTextForSpecialChars = ""
        var commaSeperatedString = ""
        isNewPasswordValid = true

        if (binding.edtNewPassword.getText().toString().length == 0) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_enter_your_password))
        } else if (Utils.hasSpecialCharacters(
                binding.edtNewPassword.editText.getText().toString(),
                Utils.splCharsPassword
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
                        Utils.ALLOWED_CHARS_PASSWORD, filterTextForSpecialChars!!
                    )
                )
            if (filterTextForSpecialChars!!.length > 0) {
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

        } else if (binding.edtNewPassword.getText().toString()
            != binding.edtConfirmPassword.getText().toString()
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_your_password_must_match))
        } else {
            binding.model = ResetPasswordModel(
                code = data?.code,
                referenceId = data?.referenceId,
                newPassword = binding.edtNewPassword.getText().toString(),
                confirmPassword = binding.edtConfirmPassword.getText().toString(),
                enable = true
            )

            isNewPasswordValid = true
            isConfirmPasswordValid = true
            binding.edtConfirmPassword.removeError()
            binding.edtNewPassword.removeError()

        }

        if (hasLowerCase(text)) {
            binding.imgDot3.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot3.setImageResource(R.drawable.circle_5dp)
        }

        if (hasUpperCase(text)) {
            binding.imgDot2.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot2.setImageResource(R.drawable.circle_5dp)
        }

        if (text.length >= 8) {
            binding.imgDot1.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot1.setImageResource(R.drawable.circle_5dp)
        }

        if (hasDigits(text)) {
            binding.imgDot4.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot4.setImageResource(R.drawable.circle_5dp)
        }
//new password

        if (isNewPasswordValid && isConfirmPasswordValid) {
            binding.btnSubmit.isEnabled = true
        } else {
            binding.btnSubmit.isEnabled = false
        }
    }

}