package com.heandroid.ui.account.profile.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.databinding.FragmentNominatedProfileConfirmEmailBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
@AndroidEntryPoint
class NominatedProfileConfirmEmailFragment  : BaseFragment<FragmentNominatedProfileConfirmEmailBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null

    private val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentNominatedProfileConfirmEmailBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().findViewById<AppCompatTextView>(R.id.tvYourDetailLabel).gone()
        binding.nominated = arguments?.getParcelable(Constants.DATA)
        binding.tvMsg.text = getString(R.string.send_security_code_msg, binding.nominated?.emailAddress)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            enable = false
            etCode.onTextChanged { enable = etCode.text.toString().trim().isNotEmpty() && etCode.text.toString().trim().length>5 }
            btnAction.setOnClickListener(this@NominatedProfileConfirmEmailFragment)
            tvResend.setOnClickListener(this@NominatedProfileConfirmEmailFragment)
        }
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
        observe(viewModel.emailValidation, ::handleEmailValidation)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> { loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                binding?.nominated?.run {
                   var request =  ProfileUpdateEmailModel(
                       referenceId="",
                     securityCode =securityCode,
                     addressLine1 ="",
                     addressLine2="",
                     city="",
                     country="",
                     emailAddress=emailAddress,
                     phoneCell="",
                     phoneDay="",
                     phoneEvening="",
                     phoneFax="",
                     primaryEmailStatus="",
                     primaryEmailUniqueID="",
                     smsOption="",
                     state="",
                     zipCode="",
                     zipCodePlus =""
                    )
                    viewModel.emailValidationForUpdatation(request)
                }

            }

            R.id.tvResend ->{
                binding.nominated?.referenceId=""
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                val request = EmailVerificationRequest(Constants.EMAIL_SELECTION_TYPE, binding.nominated?.emailAddress?:"")
                viewModel.emailVerificationApi(request)
            }
        }
    }





    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    if(resource.data?.statusCode.equals("500")) {
                        ErrorUtil.showError(binding.root, resource.data?.message)
                    }
                    else {
                        binding.nominated?.referenceId=resource.data?.referenceId
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
        } catch (e: Exception) { }
    }


    private fun handleEmailValidation(resource: Resource<EmailVerificationResponse?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    if(resource.data?.statusCode.equals("500")) {
                        ErrorUtil.showError(binding.root, resource.data?.message)
                    }
                    else {
                        val bundle= Bundle()
                        bundle.putParcelable(Constants.DATA,binding.nominated)
                        findNavController().navigate(R.id.action_confirmEmailFragment_to_emailUpdatedFragment,bundle)
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
        } catch (e: Exception) { }
    }
}