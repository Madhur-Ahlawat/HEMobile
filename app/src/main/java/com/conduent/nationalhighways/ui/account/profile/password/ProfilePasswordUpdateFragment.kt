package com.conduent.nationalhighways.ui.account.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.conduent.nationalhighways.data.model.auth.forgot.password.ResetPasswordModel
import com.conduent.nationalhighways.databinding.FragmentProfilePasswordUpdateBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePasswordUpdateFragment : BaseFragment<FragmentProfilePasswordUpdateBinding>(),
    View.OnClickListener {
    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePasswordUpdateBinding.inflate(inflater, container, false)

    override fun init() {
        checkButton()
        binding.data = ResetPasswordModel(
            currentPassword = "", newPassword = "", confirmPassword = "", enable = true
        )
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {
        binding.apply {
            tieCurrentPassword.onTextChanged { checkButton() }
            tiePassword.onTextChanged { checkButton() }
            tieConfirmPassword.onTextChanged { checkButton() }
            btnChange.setOnClickListener(this@ProfilePasswordUpdateFragment)
        }
    }

    override fun observer() {
        observe(viewModel.updatePassword, ::handleUpdatePasswordResponse)
    }

    private fun checkButton() {
        binding.enable = (binding.tiePassword.getText().toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.getText().toString().trim()
            .isNotEmpty() && binding.tieConfirmPassword.getText().toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.getText().toString()
            .trim() == binding.tiePassword.getText().toString().trim())

    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnChange -> {
                val validation = viewModel.checkPassword(binding.data)
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.updatePassword(binding.data)
                } else {
                    showError(binding.root, validation.second)
                }
            }
        }
    }

    private fun handleUpdatePasswordResponse(status: Resource<ForgotPasswordResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true) {
                    showError(binding.root, status.data.message)
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable(
                        Constants.DATA,
                        arguments?.getParcelable(Constants.DATA)
                    )
                    findNavController().navigate(
                        R.id.action_updatePasswordFragment_to_updatePasswordSuccessfulFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
                if (status.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                }else {
                    showError(binding.root, status.errorMsg)
                }
            }
            else -> {
            }
        }
    }

}