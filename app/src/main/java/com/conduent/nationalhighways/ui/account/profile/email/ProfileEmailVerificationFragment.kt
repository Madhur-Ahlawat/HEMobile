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
import com.conduent.nationalhighways.databinding.FragmentProfileEmailVerificationBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.Constants.EMAIL_SELECTION_TYPE
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils.isEmailValid
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEmailVerificationFragment : BaseFragment<FragmentProfileEmailVerificationBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileEmailVerificationBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.enable = true
        binding.data = arguments?.getParcelable(DATA)
        binding.data?.referenceId = null
    }

    override fun initCtrl() {
        binding.apply {
            etEmail.onTextChanged { enable = isEmailValid(etEmail.getText().toString()) }
            btnAction.setOnClickListener(this@ProfileEmailVerificationFragment)
        }
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btn_action -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.emailVerificationApi(
                    EmailVerificationRequest(
                        selectionType = EMAIL_SELECTION_TYPE,
                        selectionValues = binding.data?.emailAddress ?: ""
                    )
                )
            }
        }
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode?.equals("500") == true) {
                    showError(
                        binding.root,
                        resource.data.message
                    )
                } else {
                    val bundle = Bundle()
                    binding.data?.referenceId = resource.data?.referenceId
                    bundle.putParcelable(DATA, binding.data)
                    findNavController().navigate(
                        R.id.action_emailFragment_to_confirmEmailFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else {
                    showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }
}