package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.databinding.FragmentForgotPasswordBinding
import com.heandroid.hideKeyboard
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.*
import com.heandroid.utils.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), View.OnClickListener {

    @Inject
    lateinit var sessionManager : SessionManager
    private var loader: LoaderDialog?=null

    private val viewModel : ForgotPasswordViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater,container,false)

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_password_recovery))
        binding.model= ConfirmOptionModel(accountNumber = "", email = "", phone = "",enable = false)
        loader= LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.edtPostcode.addTextChangedListener { isEnable() }
        binding.edtEmail.addTextChangedListener { isEnable() }
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.confirmOption,::handleConfirmOptionResponse)
    }

    private fun handleConfirmOptionResponse(status: Resource<ConfirmOptionModel?>?) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> { val bundle = Bundle()
                                     bundle.putParcelable(Constants.OPTIONS, binding.model)
                                     findNavController().navigate(R.id.action_forgotPasswordFragment_to_chooseOptionFragment, bundle) }

            is Resource.DataError -> { showError(binding.root,status.errorMsg) }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_next -> {
                hideKeyboard()
                val validation=viewModel.validation(binding.model)
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    sessionManager.saveAccountNumber(binding.edtEmail.text.toString().trim())
                    // This is for api request
                    binding.model?.apply {
                        accountNumber="4294274"
                        email="christoper@gmail.com"
                        phone="9823233232"
                    }
                    viewModel.confirmOptionForForgot(binding.model)
                }else{
                    showError(binding.root,validation.second)
                }
            }
        }
    }

    private fun isEnable(){
        if(binding.edtEmail.length()>0 && binding.edtPostcode.length()>0) binding.model = ConfirmOptionModel(enable = true, accountNumber = "", email = binding.edtEmail.text.toString(), phone = binding.edtPostcode.text.toString())
        else binding.model = ConfirmOptionModel(enable = false, accountNumber = "", email = binding.edtEmail.text.toString(), phone = binding.edtPostcode.text.toString())
    }
}