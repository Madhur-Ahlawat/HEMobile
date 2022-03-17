package com.heandroid.ui.account.creation.step1

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
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class CreateAccountConfirmEmailFragment : BaseFragment<FragmentCreateAccountConfirmEmailBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val createAccountViewModel: CreateAccountEmailViewModel by viewModels()

    private var refId: Long? = null
    private var requestModel : CreateAccountRequestModel? =null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountConfirmEmailBinding.inflate(inflater, container, false)


    override fun init() {
        requestModel = arguments?.getParcelable(DATA)
        refId = arguments?.getLong(Constants.REFERENCE_ID)
        binding.tvMsg.text = getString(R.string.send_security_code_msg, requestModel?.emailAddress)
        binding.tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 1, 5)
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
            R.id.btnAction -> { confirmEmailCode() }
            R.id.tvResend -> { sendEmailVerificationRequest() }
        }
    }

    private fun sendEmailVerificationRequest() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = EmailVerificationRequest(Constants.EMAIL_SELECTION_TYPE, requestModel?.emailAddress?:"")
        createAccountViewModel.emailVerificationApi(request)
    }

    private fun confirmEmailCode() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = ConfirmEmailRequest(refId.toString(), requestModel?.emailAddress?:"", binding.etCode.text.toString().trim())
        createAccountViewModel.confirmEmailApi(request)
    }


    private fun isEnable() {
        if (binding.etCode.text.toString().trim().isNotEmpty()) binding.model = EmailValidationModel(enable = true, email = requestModel?.emailAddress?:"", code = binding.etCode.text.toString())
        else binding.model = EmailValidationModel(enable = false, email = requestModel?.emailAddress?:"", code = binding.etCode.text.toString())
    }


    private fun handleConfirmEmailResponse(resource: Resource<EmptyApiResponse?>?) {
        // todo must be removed, only for testing the flow
        val bundle = Bundle()
        bundle.putParcelable(DATA,requestModel)
        findNavController().navigate(R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment,bundle)
        //
        try{
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if(resource.data?.status?.equals("500")==true){
//                   showError(binding.root,resource.data.message)
                }else{
                    val bundle = Bundle()
                    bundle.putParcelable(DATA,requestModel)
                    findNavController().navigate(R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment,bundle)
                }
            }
            is Resource.DataError -> {
//                showError(binding.root, resource.errorMsg)
            }
        }}catch (e: Exception){}
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    requireContext().showToast("code sent successfully")
                    refId=resource.data?.referenceID?:0
                }
                is Resource.DataError -> { showError(binding.root, resource.errorMsg) }
            }
        }catch (e:Exception){ }
    }
}