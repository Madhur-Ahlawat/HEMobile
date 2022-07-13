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
import com.heandroid.databinding.FragmentProfileEmailVerificationBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Constants.EMAIL_SELECTION_TYPE
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Utils.isEmailValid
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEmailVerificationFragment : BaseFragment<FragmentProfileEmailVerificationBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val viewModel : ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentProfileEmailVerificationBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().findViewById<AppCompatTextView>(R.id.tvYourDetailLabel).gone()

        loader = LoaderDialog()
       loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

       binding.enable = true
       binding.data = arguments?.getParcelable(DATA)
       binding.data?.referenceId=null
    }

    override fun initCtrl() {
        binding.apply {
            etEmail.onTextChanged { enable = isEmailValid(etEmail.text.toString()) }
            btnAction.setOnClickListener(this@ProfileEmailVerificationFragment)
        }
    }

    override fun observer() {
        observe(viewModel.emailVerificationApiVal, ::handleEmailVerification)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
           R.id.btn_action -> { loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                                 viewModel.emailVerificationApi(EmailVerificationRequest(selectionType = EMAIL_SELECTION_TYPE, selectionValues = binding.data?.emailAddress?:"")) }
       }
    }

    private fun handleEmailVerification(resource: Resource<EmailVerificationResponse?>?) {
        try{
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    if(resource.data?.statusCode?.equals("500")==true) { showError(binding.root,resource.data?.message)}
                    else{
                        val bundle = Bundle()
                        binding.data?.referenceId = resource.data?.referenceId
                        bundle.putParcelable(DATA,binding.data)
                        findNavController().navigate(R.id.action_emailFragment_to_confirmEmailFragment,bundle)
                    }
                }
                is Resource.DataError -> { showError(binding.root, resource.errorMsg) }
            }}catch (e: Exception){}
    }
}