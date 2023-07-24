package com.conduent.nationalhighways.ui.account.profile.password

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.conduent.nationalhighways.utils.common.Utils.hasSpecialCharacters
import com.conduent.nationalhighways.utils.common.Utils.hasUpperCase
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordProfileFragment : BaseFragment<FragmentChangePasswordProfileBinding>(),
    View.OnClickListener {
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
        binding.edtConfirmPassword.editText.addTextChangedListener { isEnable1() }



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
                    newPassword = binding.edtNewPassword.editText.text.toString().trim(),
                    currentPassword = binding.edtCurrentPassword.editText.text.toString().trim(),
                    confirmPassword = binding.edtConfirmPassword.editText.text.toString().trim()
                )
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
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

                    findNavController().navigate(
                        R.id.action_changePasswordProfile_to_profileParsswordSuccessFragment
                    )



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
        if (binding.edtNewPassword.getText().toString().trim().isNotEmpty()
            && binding.edtConfirmPassword.getText().toString().trim().isNotEmpty()
            && ((binding.edtNewPassword.getText().toString().trim().length) > 7)
            && ((binding.edtConfirmPassword.getText().toString()
                .trim().length) > 7) && (binding.edtNewPassword.getText().toString()
                .trim() == binding.edtConfirmPassword.getText().toString().trim())
        ) {
            binding.btnSubmit.isEnabled = true

        } else {
            binding.btnSubmit.isEnabled = false
        }

        if (binding.edtNewPassword.getText().toString()
                .trim() == binding.edtConfirmPassword.getText().toString().trim()
        ) {
            binding.edtConfirmPassword.removeError()
        } else {
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_your_password_must_match))
        }


    }

    private fun isEnable(text: String) {
        if (binding.edtNewPassword.getText().toString().trim().isNotEmpty()
            && binding.edtConfirmPassword.getText().toString().trim().isNotEmpty()
            && ((binding.edtNewPassword.getText().toString().trim().length) > 7)
            && ((binding.edtConfirmPassword.getText().toString()
                .trim().length) > 7 && (binding.edtNewPassword.getText().toString()
                .trim() == binding.edtConfirmPassword.getText().toString().trim()))
        ) {
            binding.btnSubmit.isEnabled = true
        } else {
            binding.btnSubmit.isEnabled = false
        }


        if (binding.edtNewPassword.getText().toString().trim().length < 3) {

            binding.edtNewPassword.setErrorText(getString(R.string.password_must_be_8_characters))

        } else if (!binding.edtNewPassword.getText().toString().trim().contains(Utils.NUMBER)) {

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_contain_at_least_one_character))

        } else if (!binding.edtNewPassword.getText().toString().trim().contains(Utils.UPPERCASE)) {

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_upper_case))

        } else if (!binding.edtNewPassword.getText().toString().trim().contains(Utils.LOWECASE)) {

            binding.edtNewPassword.setErrorText(getString(R.string.str_password_must_least_contain_one_lower_case))

        } else if (hasSpecialCharacters(text)) {
            binding.edtNewPassword.setErrorText(getString(R.string.password_must_not_have_special_characters))
        } else {
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

        if (text.trim().length > 7) {
            binding.imgDot1.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot1.setImageResource(R.drawable.circle_5dp)
        }

        if (hasDigits(text)) {
            binding.imgDot4.setImageResource(R.drawable.grin_tick)
        } else {
            binding.imgDot4.setImageResource(R.drawable.circle_5dp)
        }

    }

}