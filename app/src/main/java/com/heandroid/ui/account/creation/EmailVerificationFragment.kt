package com.heandroid.ui.account.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.EmailValidationModel
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.databinding.FragmentEmailVerificationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EmailVerificationFragment : BaseFragment<FragmentEmailVerificationBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEmailVerificationBinding.inflate(inflater, container, false)


    override fun init() {
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            model = EmailValidationModel(false, "", "")
            etEmail.onTextChanged {
                isEnable()
            }
            btnAction.setOnClickListener(this@EmailVerificationFragment)
        }
    }

    override fun observer() {
        observe(createAccountViewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        loader?.dismiss()
        resource?.let {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.referenceID?.let { ref ->
                        val bundle = Bundle().apply {
                            putString(Constants.EMAIL, binding.etEmail.text.toString().trim())
                            putLong(Constants.REFERENCE_ID, ref)
                        }
                        findNavController().navigate(R.id.action_emailVerification_to_confirmEmailFragment, bundle)
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {

                }
            }
        }
    }


    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btn_action -> {
                    hideKeyboard()
                    sendEmailVerificationRequest()
                }
            }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = EmailVerificationRequest(
            Constants.EMAIL_SELECTION_TYPE,
            binding.etEmail.text.toString().trim()
        )
        createAccountViewModel.emailVerificationApi(request)
    }

    private fun isEnable() {
        if (Utils.isEmailValid(binding.etEmail.text.toString()))
            binding.model = EmailValidationModel(
                enable = true,
                email = binding.etEmail.text.toString(),
                ""
            )
        else
            binding.model = EmailValidationModel(
                enable = false,
                email = binding.etEmail.text.toString(),
                ""
            )
    }
}