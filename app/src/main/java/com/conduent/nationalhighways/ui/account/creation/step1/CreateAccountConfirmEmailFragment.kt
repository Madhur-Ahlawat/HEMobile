package com.conduent.nationalhighways.ui.account.creation.step1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.EmailValidationModel
import com.conduent.nationalhighways.data.model.createaccount.ConfirmEmailRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.databinding.FragmentCreateAccountConfirmEmailBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountConfirmEmailFragment : BaseFragment<FragmentCreateAccountConfirmEmailBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()
    private var requestModel: CreateAccountRequestModel? = null
    private var isEditEmail: Int? = null
    private var count = 1
    private var isCodeCheckApi = true
    private var isClicked = false


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountConfirmEmailBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel = arguments?.getParcelable(CREATE_ACCOUNT_DATA)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 6)
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
        isClicked = true
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
        if (isClicked) {
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
            isClicked = false
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
        if (isCodeCheckApi) {
            count++
            binding.btnAction.performClick()

        } else {
            count++
            binding.tvResend.performClick()

        }
    }
}