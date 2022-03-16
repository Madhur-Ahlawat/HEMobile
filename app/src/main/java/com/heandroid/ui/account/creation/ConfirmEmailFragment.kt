package com.heandroid.ui.account.creation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.databinding.FragmentConfirmEmailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmEmailFragment : BaseFragment<FragmentConfirmEmailBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private var email: String? = null
    private var refId: Long? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentConfirmEmailBinding.inflate(inflater, container, false)


    override fun init() {
        email = arguments?.getString(Constants.EMAIL)
        refId = arguments?.getLong(Constants.REFERENCE_ID)
        binding.tvMsg.text = getString(R.string.send_security_code_msg, email)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            model = EmailValidationModel(false, "", "")
            etCode.onTextChanged {
                isEnable()
            }
            btnAction.setOnClickListener(this@ConfirmEmailFragment)
            tvResend.setOnClickListener(this@ConfirmEmailFragment)
        }
    }

    override fun observer() {
        observe(createAccountViewModel.confirmEmailApiVal, ::handleConfirmEmailResponse)
        observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    private fun handleConfirmEmailResponse(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        resource?.let {
            when (resource) {
                is Resource.Success -> {
                    navigateNextScreen()
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }
        }
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        loader?.dismiss()
        resource?.let {
            when (resource) {
                is Resource.Success -> {
                    requireContext().showToast("code sent successfully")
                    resource.data?.referenceID?.let { ref ->
                        refId = ref
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> { }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btnAction -> {
                    hideKeyboard()
                    confirmEmailCode()
                }
                R.id.tvResend -> {
                    sendEmailVerificationRequest()
                }
            }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = email?.let {
            EmailVerificationRequest(
                Constants.EMAIL_SELECTION_TYPE,
                it
            )
        }
        createAccountViewModel.emailVerificationApi(request)
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = email?.let {
            ConfirmEmailRequest(
                refId.toString(),
                it,
                binding.etCode.text.toString().trim()
            )
        }
        request?.let {
            createAccountViewModel.confirmEmailApi(request)
        }
    }


    private fun navigateNextScreen() {
        findNavController().navigate(R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment)
    }

    private fun isEnable() {
        if (binding.etCode.text.toString().trim().isNotEmpty())
            binding.model = EmailValidationModel(
                enable = true,
                email = "",
                code = binding.etCode.text.toString()
            )
        else
            binding.model = EmailValidationModel(
                enable = false,
                email = "",
                code = binding.etCode.text.toString()
            )
    }
}