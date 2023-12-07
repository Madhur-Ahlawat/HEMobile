package com.conduent.nationalhighways.ui.account.profile.email

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.UserNameCheckReq
import com.conduent.nationalhighways.data.model.auth.forgot.password.RequestOTPModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentChangeEmailProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.ACCOUNTINFORMATION
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PERSONALDATA
import com.conduent.nationalhighways.utils.common.Constants.REPLENISHMENTINFORMATION
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FragmentChangeEmailProfile : BaseFragment<FragmentChangeEmailProfileBinding>() {

    private var btnEnabled: Boolean = false
    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()
    private var data: ProfileDetailModel? = null
private var oldEmail=""
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChangeEmailProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navData?.let {
            data = it as ProfileDetailModel
            binding.edtEmail.setText(data?.personalInformation?.userName?.lowercase(Locale.getDefault()))
            oldEmail=data?.personalInformation?.userName?.lowercase(Locale.getDefault())?:""
        }
        HomeActivityMain.setTitle(getString(R.string.profile_email_addres))
        (requireActivity() as HomeActivityMain).showHideToolbar(true)
    }

    override fun initCtrl() {
        binding.apply {
            edtEmail.addTextChangedListener {
                enable = isEnable()
            }

            btnNext.setOnClickListener {
                hideKeyboard()
                if(oldEmail.equals(binding.edtEmail.text.toString().lowercase())){
                    findNavController().popBackStack()
                }else{
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    val request = UserNameCheckReq(binding.edtEmail.text.toString().trim())
                    viewModel.userNameAvailabilityCheck(request)
                }

//                viewModel.emailVerificationApi(
//                    EmailVerificationRequest(
//                        selectionType = EMAIL_SELECTION_TYPE,
//                        selectionValues = binding.edtEmail.getText().toString().trim().replace(" ","") ?: ""
//                    )
//                )
            }
            isEnable()
            checkButton()
        }
    }

    fun checkButton() {
        binding.btnNext?.isEnabled = btnEnabled
        binding.btnNext?.isFocusable = btnEnabled
    }

    private var commaSeperatedString: String? = null
    private var filterTextForSpecialChars: String? = null
    private fun isEnable(): Boolean {
        btnEnabled =
            if (binding.edtEmail.text.toString().trim().length > 0) {
                if (binding.edtEmail.text.toString().trim().length < 8) {
                    binding.txtError.visible()
                    binding.txtError.text = getString(R.string.email_address_must_be_8_characters_or_more)
                    false
                } else {
                    if (binding.edtEmail.text.toString().length > 100) {
                        false
                    } else {
                        if (!Utils.isLastCharOfStringACharacter(
                                binding.edtEmail.text.toString().trim()
                            ) || Utils.countOccurenceOfChar(
                                binding.edtEmail.text.toString().trim(), '@'
                            ) > 1 || binding.edtEmail.text.toString().trim().contains(
                                Utils.TWO_OR_MORE_DOTS
                            ) || (binding.edtEmail.text.toString().trim().last()
                                .toString().equals(".") || binding.edtEmail.text
                                .toString().first().toString().equals("."))
                            || (binding.edtEmail.text.toString().trim().last().toString()
                                .equals("-") || binding.edtEmail.text.toString().first()
                                .toString().equals("-"))
                            || (Utils.countOccurenceOfChar(
                                binding.edtEmail.text.toString().trim(), '.'
                            ) < 1) || (Utils.countOccurenceOfChar(
                                binding.edtEmail.text.toString().trim(), '@'
                            ) < 1)
                        ) {
                            binding.txtError.visible()
                            binding.txtError.text = getString(R.string.str_email_format_error_message)
                            false
                        } else {
                            if (Utils.hasSpecialCharacters(
                                    binding.edtEmail.text.toString().trim(),
                                    Utils.splCharEmailCode
                                )
                            ) {
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.LOWER_CASE,
                                        binding.edtEmail.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.UPPER_CASE,
                                        binding.edtEmail.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.DIGITS,
                                        binding.edtEmail.text.toString().trim()
                                    )
                                filterTextForSpecialChars =
                                    Utils.removeGivenStringCharactersFromString(
                                        Utils.ALLOWED_CHARS_EMAIL,
                                        binding.edtEmail.text.toString().trim()
                                    )
                                commaSeperatedString =
                                    Utils.makeCommaSeperatedStringForPassword(
                                        Utils.removeAllCharacters(
                                            Utils.ALLOWED_CHARS_EMAIL, filterTextForSpecialChars!!
                                        )
                                    )
                                if (filterTextForSpecialChars!!.length > 0) {
                                    binding.txtError.visible()
                                    binding.txtError.text = "Email address must not include $commaSeperatedString"
                                    false
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                        binding.edtEmail.text.toString()
                                    ).matches()
                                ) {
                                    binding.txtError.visible()
                                    binding.txtError.text = getString(R.string.str_email_format_error_message)
                                    false
                                } else {
                                    binding.txtError.gone()
                                    binding.txtError.text = ""
                                    true
                                }
                            } else if (!(Utils.countOccurenceOfChar(
                                    binding.edtEmail.text.toString().trim(), '@'
                                ) > 0 && Utils.countOccurenceOfChar(
                                    binding.edtEmail.text.toString().trim(), '@'
                                ) < 2)
                            ) {
                                binding.txtError.visible()
                                binding.txtError.text = getString(R.string.str_email_format_error_message)
                                false
                            } else {
                                binding.txtError.gone()
                                binding.txtError.text = ""
                                true
                            }
                        }
                    }
                }
            } else {
                binding.txtError.gone()
                binding.txtError.text = ""
                false
            }
        return btnEnabled
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
        observe(viewModel.userNameAvailabilityCheck, ::handleEmailCheck)

    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                val bundle = Bundle()
                binding.data?.referenceId = resource.data?.referenceId
                bundle.putParcelable(NAV_DATA_KEY, binding.data)
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(
                    REPLENISHMENTINFORMATION,
                    HomeActivityMain.accountDetailsData?.replenishmentInformation
                )
                bundle.putParcelable(
                    PERSONALDATA,
                    HomeActivityMain.accountDetailsData?.personalInformation?.apply {
                        emailAddress = binding.edtEmail.text.toString().trim()
                    })
                bundle.putString(Constants.REFERENCE_ID, resource.data?.referenceId)
                bundle.putBoolean(
                    Constants.IS_EDIT_EMAIL,
                    arguments?.getBoolean(Constants.IS_EDIT_EMAIL) as Boolean
                )
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.AccountType_EMAIL)
                bundle.putParcelable(
                    "response",
                    SecurityCodeResponseModel(
                        resource.data?.emailStatusCode,
                        0L,
                        resource.data?.referenceId,
                        true
                    )
                )
                bundle.putParcelable(
                    "data",
                    RequestOTPModel(Constants.EMAIL, binding.edtEmail.text.toString().trim())
                )

                findNavController().navigate(
                    R.id.action_change_email_profile_to_confirm_security_code,
                    bundle
                )

            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
        viewModel._emailVerificationApiVal.postValue(null)
    }

    private fun handleEmailCheck(response: Resource<Boolean?>?) {

        if (response?.data == true) {
            val request = EmailVerificationRequest(
                Constants.EMAIL,
                binding.edtEmail.text.toString().trim()
            )
            viewModel.emailVerificationApi(request)
        } else {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            if (navFlowCall == Constants.ACCOUNT_CREATION_EMAIL_FLOW || navFlowCall == Constants.PROFILE_MANAGEMENT) {
                binding.txtError.visible()
                binding.txtError.text = getString(R.string.an_account_with_this_email_address_already_exists)
            }

        }

    }
}