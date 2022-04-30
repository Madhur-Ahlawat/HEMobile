package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.databinding.FragmentForgotPasswordBinding
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager : SessionManager
    private var loader: LoaderDialog?=null

    private val viewModel : ForgotPasswordViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater,container,false)

    override fun init() {
        sessionManager.clearAll()

        requireActivity().toolbar(getString(R.string.str_password_recovery))
        binding.model= ConfirmOptionModel(identifier = "", zipCode = "",enable = false)
        loader= LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.edtPostcode.addTextChangedListener { isEnable() }
        binding.edtEmail.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
          observe(viewModel.confirmOption,::handleConfirmOptionResponse)
        }
    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionResponseModel?>?) {
        try{
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("1054")==true) {
                    showError(binding.root,status.data?.message)
                } else {
                    binding?.root?.post {
                        val bundle = Bundle()
                        bundle.putParcelable(Constants.OPTIONS, status.data)
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_chooseOptionFragment, bundle)
                    }
                }
            }
            is Resource.DataError -> { showError(binding.root,status.errorMsg) }
        }}catch (e: Exception){}
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_next -> {
                hideKeyboard()
                val validation=viewModel.validation(binding.model)
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    sessionManager.saveAccountNumber(binding.edtEmail.text.toString().trim())
                    viewModel.confirmOptionForForgot(binding.model)
                }else{
                    showError(binding.root,validation.second)
                }
            }
        }
    }

    private fun isEnable(){
        if(binding.edtEmail.length()>0 && binding.edtPostcode.length()>0) binding.model = ConfirmOptionModel(enable = true, identifier = binding.edtEmail.text.toString(), zipCode = binding.edtPostcode.text.toString())
        else binding.model = ConfirmOptionModel(enable = false, identifier = binding.edtEmail.text.toString(), zipCode = binding.edtPostcode.text.toString())
    }

}