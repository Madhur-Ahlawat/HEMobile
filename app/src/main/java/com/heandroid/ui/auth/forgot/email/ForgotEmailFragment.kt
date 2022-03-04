package com.heandroid.ui.auth.forgot.email

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.databinding.FragmentForgotEmailBinding
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.observe
import com.heandroid.utils.common.observeOnce
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotEmailFragment : BaseFragment<FragmentForgotEmailBinding>(), View.OnClickListener {

    private var loader: LoaderDialog?=null

    @Inject
    lateinit var sessionManager : SessionManager

    private val viewModel : ForgotEmailViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotEmailBinding = FragmentForgotEmailBinding.inflate(inflater,container,false)

    override fun init() {
        sessionManager.clearAll()
        requireActivity().toolbar(getString(R.string.txt_recovery_username))
        binding.model= ForgotEmailModel(enable = false, accountNumber = "", zipCode = "")
        loader= LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            edtAccountNumber.addTextChangedListener { isEnable() }
            edtPostCode.addTextChangedListener { isEnable() }
            btnNext.setOnClickListener(this@ForgotEmailFragment)
            btnLogin.setOnClickListener(this@ForgotEmailFragment)
        }
    }

    override fun observer() {
        observe(viewModel.forgotEmail,::handleForgotEmail)
    }

    override fun onClick(v: View?) {
        v?.let {
            when(v.id){
                R.id.btn_next -> {
                    hideKeyboard()
                    val validation=viewModel.validation(binding.model)
                    if(validation.first){
                        loader?.show(requireActivity().supportFragmentManager,"")
                        viewModel.forgotEmail(binding.model)
                    }else {
                        showError(binding.root,validation.second)
                    }
                }

                R.id.btn_login -> { requireActivity().onBackPressed() }
            }
        }

    }

    private fun handleForgotEmail(status: Resource<ForgotEmailResponseModel?>?){
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                binding.llEnterDetails.visibility = GONE
                binding.llUsername.visibility = VISIBLE
                loadData(status)
            }
            is Resource.DataError -> { showError(binding.root, status.errorMsg) }
            else -> {}

        }

    }

    private fun loadData(status: Resource.Success<ForgotEmailResponseModel?>) {
        val username=viewModel.loadUserName(status.data?.userName?:"")
        binding.tvUsername.text = username.toString()
    }

    private fun isEnable() {
        if(binding.edtAccountNumber.length()>0 && binding.edtPostCode.length()>0) binding.model = ForgotEmailModel(enable = true, accountNumber = binding.edtAccountNumber.text.toString(), zipCode = binding.edtPostCode.text.toString())
        else binding.model = ForgotEmailModel(enable = false, accountNumber = binding.edtAccountNumber.text.toString(), zipCode = binding.edtPostCode.text.toString())
    }
}