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
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentChangePasswordProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
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
import com.conduent.nationalhighways.utils.setPersonalInfoAnnouncement
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
    private var profileDetailModel: ProfileDetailModel? = null
    private var loader: LoaderDialog? = null
    private var passwordVisible: Boolean = false
    private var confirmPasswordVisible: Boolean = false


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
        navData?.let {
            if (it is ProfileDetailModel) {
                profileDetailModel = navData as ProfileDetailModel
            }
        }
        data = arguments?.getParcelable("data")
        HomeActivityMain.setTitle(getString(R.string.profile_password))
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initCtrl() {
        setPersonalInfoAnnouncement(binding.rootLayout,requireActivity())
        binding.btnSubmit.setOnClickListener(this)
        binding.edtCurrentPassword.editText.addTextChangedListener { currentPasswordListener() }
        binding.edtNewPassword.editText.addTextChangedListener { isEnable(it.toString()) }
        binding.edtConfirmPassword.editText.addTextChangedListener { isEnable1() }

        binding.edtNewPassword.setOnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtNewPassword.right - binding.edtNewPassword.editText.compoundDrawables[right].bounds.width()) {
                    if (passwordVisible) {
                        binding.edtNewPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtNewPassword.editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        binding.edtNewPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtNewPassword.editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                }
            }

            false
        }


        binding.edtConfirmPassword.setOnTouchListener { _, event ->

            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edtConfirmPassword.right - binding.edtConfirmPassword.editText.compoundDrawables[right].bounds.width()) {

                    if (confirmPasswordVisible) {
                        binding.edtConfirmPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_24, 0
                        )
                        binding.edtConfirmPassword.editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        confirmPasswordVisible = false
                    } else {

                        binding.edtConfirmPassword.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0, 0, R.drawable.ic_baseline_visibility_off_24, 0
                        )
                        binding.edtConfirmPassword.editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        confirmPasswordVisible = true
                    }
                }
            }

            false
        }


    }

    private fun currentPasswordListener() {
        if (binding.edtCurrentPassword.editText.text.toString().trim().isEmpty()) {
            disableButton()
        } else if (binding.edtCurrentPassword.editText.text.toString().isNotEmpty() &&
            binding.edtNewPassword.editText.text.toString()
                .isNotEmpty() && binding.edtCurrentPassword.editText.text.toString() == binding.edtNewPassword.editText.text.toString()
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
            binding.edtNewPassword.setErrorText(getString(R.string.str_newpassword_cannot_be_same_current_password))
            disableButton()
        } else {
            isEnable(binding.edtNewPassword.editText.toString())
        }
        binding.edtCurrentPassword.removeError()
    }

    override fun observer() {
        observe(viewModel.updatePassword, ::handleResetResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_submit -> {
                hideKeyboard()
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    disableButton()
                    viewModel.updatePassword(
                        ResetPasswordModel(
                            currentPassword = binding.edtCurrentPassword.editText.text.toString(),
                            newPassword = binding.edtNewPassword.editText.text.toString(),
                            confirmPassword = binding.edtConfirmPassword.editText.text.toString()
                        )
                    )
            }
        }
    }

    private fun enableButton() {
        binding.btnSubmit.isEnabled = true
        binding.btnSubmit.isFocusable = true
    }

    private fun disableButton() {
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.isFocusable = false
    }

    private fun handleResetResponse(status: Resource<ForgotPasswordResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if(status.data?.statusCode=="1308"){
                    binding.edtCurrentPassword.setErrorText(status.data.message?:"")
                    enableButton()
                }
               else if (status.data?.statusCode != "1308" && status.data?.message?.lowercase(Locale.ROOT)?.contains("success")==true
                ) {
                    sessionManager.clearAll()
                    Intent(
                        requireActivity(),
                        ProfilePasswordChangeSuccessActivity::class.java
                    ).apply {
                        putExtra(
                            Constants.EMAIL,
                            profileDetailModel?.personalInformation?.emailAddress
                        )
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

    private fun isEnable1() {
        isConfirmPasswordValid = true

        if (binding.edtConfirmPassword.getText().toString().isEmpty()) {
            isConfirmPasswordValid = false
            binding.edtConfirmPassword.removeError()
        } else if (binding.edtNewPassword.getText()
                .toString() != binding.edtConfirmPassword.getText().toString()
        ) {
            isConfirmPasswordValid = false
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_new_your_password_must_match))
        } else if (binding.edtCurrentPassword.editText.text.toString() == binding.edtNewPassword.editText.text.toString()
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_newpassword_cannot_be_same_current_password))
            binding.edtConfirmPassword.removeError()
        } else {
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
            isEnable(binding.edtNewPassword.editText.toString())
        }

//        binding.btnSubmit.isEnabled = isNewPasswordValid && isConfirmPasswordValid
    }

    private fun isEnable(text: String) {
        var filterTextForSpecialChars = ""
        var commaSeparatedString = ""
        isNewPasswordValid = true

        if (binding.edtNewPassword.getText().toString().isEmpty()) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
        } else if (Utils.hasSpecialCharacters(
                binding.edtNewPassword.editText.text.toString(),
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
            commaSeparatedString =
                Utils.makeCommaSeperatedStringForPassword(
                    Utils.removeAllCharacters(
                        Utils.ALLOWED_CHARS_PASSWORD, filterTextForSpecialChars
                    )
                )
            if (filterTextForSpecialChars.isNotEmpty()) {
                binding.edtNewPassword.setErrorText(resources.getString(R.string.str_password_must_not_include_disallowed_character,commaSeparatedString))
            } else {
                binding.edtNewPassword.removeError()
            }
        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.NUMBER)) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_new_password_must_contain_at_least_one_character))
        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.UPPERCASE)) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_new_password_must_least_contain_one_upper_case))

        } else if (!binding.edtNewPassword.getText().toString().contains(Utils.LOWECASE)) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_new_password_must_least_contain_one_lower_case))
        } else if (binding.edtNewPassword.getText().toString().length < 8) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_new_password_must_be_8_characters))

        } else if (binding.edtConfirmPassword.getText().toString()
                .isNotEmpty() && (binding.edtNewPassword.getText().toString()
                    != binding.edtConfirmPassword.getText().toString())
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.removeError()
            binding.edtConfirmPassword.setErrorText(getString(R.string.str_new_your_password_must_match))
        } else if (binding.edtCurrentPassword.editText.text.toString() == binding.edtNewPassword.editText.text.toString()
        ) {
            isNewPasswordValid = false
            binding.edtNewPassword.setErrorText(getString(R.string.str_newpassword_cannot_be_same_current_password))
            binding.edtConfirmPassword.removeError()

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

        binding.btnSubmit.isEnabled = isNewPasswordValid && isConfirmPasswordValid
    }
}