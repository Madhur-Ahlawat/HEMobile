package com.heandroid.ui.account.creation.step1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.databinding.FragmentCreateAccountConfirmEmailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.loader.OnRetryClickListener
import com.heandroid.utils.common.*
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.makeLinks
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class CreateAccountConfirmEmailFragment : BaseFragment<FragmentCreateAccountConfirmEmailBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var requestModel: CreateAccountRequestModel? = null
    private var isEditEmail: Int? = null
    private var count = 1
    private var isCodeCheckApi = true


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountConfirmEmailBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
        binding.tvMsg.text = getString(R.string.send_security_code_msg, requestModel?.emailAddress)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL) == true) {
            isEditEmail = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL)
        }
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            model = EmailValidationModel(false, "", "")
            etCode.onTextChanged { isEnable() }
            btnAction.setOnClickListener(this@CreateAccountConfirmEmailFragment)
            tvResend.setOnClickListener(this@CreateAccountConfirmEmailFragment)
//            tvMsg.makeLinks(Pair("resend security code",View.OnClickListener {
//                sendEmailVerificationRequest()
//
//            }))
        }
    }

    override fun observer() {
        observe(createAccountViewModel.confirmEmailApiVal, ::handleConfirmEmailResponse)
        observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                confirmEmailCode()
            }
            R.id.tvResend -> {
                sendEmailVerificationRequest()
            }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = EmailVerificationRequest(
            Constants.EMAIL_SELECTION_TYPE,
            requestModel?.emailAddress ?: ""
        )
        createAccountViewModel.emailVerificationApi(request)
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val request = ConfirmEmailRequest(
            requestModel?.referenceId?.toString() ?: "",
            requestModel?.emailAddress ?: "",
            binding.etCode.text.toString().trim()
        )
        createAccountViewModel.confirmEmailApi(request)
    }


    private fun isEnable() {
        if (binding.etCode.text.toString().trim().isNotEmpty()) binding.model =
            EmailValidationModel(
                enable = true,
                email = requestModel?.emailAddress ?: "",
                code = binding.etCode.text.toString()
            )
        else binding.model = EmailValidationModel(
            enable = false,
            email = requestModel?.emailAddress ?: "",
            code = binding.etCode.text.toString()
        )
    }


    private fun handleConfirmEmailResponse(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        isCodeCheckApi = true
        when (resource) {
            is Resource.Success -> {
                count = 1

                if (resource.data?.status?.equals("500") == true) showError(
                    binding.root,
                    resource.data.message
                )
                else loadFragment()
            }
            is Resource.DataError -> {
                if (resource.errorMsg.contains("Connect your VPN", true)) {
                    if (count > Constants.RETRY_COUNT) {
                        requireActivity().startActivity(
                            Intent(context, LandingActivity::class.java)
                                .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    ErrorUtil.showRetry(this)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }

    private fun loadFragment() {
        requestModel?.securityCd = binding.etCode.text.toString().trim().toLongOrNull()
        val bundle = Bundle()
        bundle.putParcelable(CREATE_ACCOUNT_DATA, requestModel)
        isEditEmail?.let {
            findNavController().navigate(
                R.id.action_confirmEmailFragment_to_paymentSummaryFragment,
                bundle
            )
        } ?: run {
            findNavController().navigate(
                R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment,
                bundle
            )
        }

    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        isCodeCheckApi = false
        when (resource) {
            is Resource.Success -> {
                count = 1
                requireContext().showToast("code sent successfully")
                requestModel?.referenceId = resource.data?.referenceId?.toLongOrNull()
            }
            is Resource.DataError -> {
                if (resource.errorMsg.contains("Connect your VPN", true)) {
                    if (count > Constants.RETRY_COUNT) {
                        requireActivity().startActivity(
                            Intent(context, LandingActivity::class.java)
                                .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    ErrorUtil.showRetry(this)
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }

    override fun onRetryClick() {
        if(isCodeCheckApi){
            count++
            binding.btnAction.performClick()

        }else{
            count++
            binding.tvResend.performClick()

        }
    }
}