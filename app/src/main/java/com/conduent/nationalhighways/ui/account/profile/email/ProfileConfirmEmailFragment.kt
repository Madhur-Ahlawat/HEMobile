package com.conduent.nationalhighways.ui.account.profile.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationRequest
import com.conduent.nationalhighways.data.model.createaccount.EmailVerificationResponse
import com.conduent.nationalhighways.databinding.FragmentProfileConfirmEmailBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileConfirmEmailFragment : BaseFragment<FragmentProfileConfirmEmailBinding>(),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileConfirmEmailBinding.inflate(inflater, container, false)

    override fun init() {
        binding.data = arguments?.getParcelable(DATA)
        binding.tvMsg.text = getString(R.string.send_security_code_msg, binding.data?.emailAddress)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            enable = false
            etCode.onTextChanged {
                enable = etCode.getText().toString().trim().isNotEmpty() && etCode.text.toString()
                    .trim().length > 5
            }
            btnAction.setOnClickListener(this@ProfileConfirmEmailFragment)
            tvResend.setOnClickListener(this@ProfileConfirmEmailFragment)
        }
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
        observe(viewModel.emailValidation, ::handleEmailValidation)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.emailValidationForUpdatation(binding.data)
            }

            R.id.tvResend -> {
                binding.data?.referenceId = ""
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                val request = EmailVerificationRequest(
                    Constants.EMAIL_SELECTION_TYPE,
                    binding.data?.emailAddress ?: ""
                )
                viewModel.emailVerificationApi(request)
            }
        }
    }


    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode.equals("500")) {
                    showError(binding.root, resource.data?.message)
                } else {
                    binding.data?.referenceId = resource.data?.referenceId
                }
            }
            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }
            else -> {}
        }
    }


    private fun handleEmailValidation(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode.equals("500")) {
                    showError(binding.root, resource.data?.message)
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable(DATA, binding.data)
                    findNavController().navigate(
                        R.id.action_confirmEmailFragment_to_emailUpdatedFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                }else {
                    showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }
}
