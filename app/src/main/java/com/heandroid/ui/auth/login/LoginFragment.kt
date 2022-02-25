package com.heandroid.ui.auth.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.databinding.FragmentLoginBinding
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.*
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModels()
    private var loader: LoaderDialog?=null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false)

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_log_in_dart_system))
    }
    override fun init() {
        binding.model= LoginModel(value = "", password = "", enable = false)
        loader= LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            edtEmail.addTextChangedListener { isEnable() }
            edtPwd.addTextChangedListener { isEnable() }
            tvForgotUsername.setOnClickListener(this@LoginFragment)
            tvForgotPassword.setOnClickListener(this@LoginFragment)
            btnLogin.setOnClickListener(this@LoginFragment)
        }

    }

    override fun observer() {
        observe(viewModel.login, ::handleLoginResponse)
    }

    private fun handleLoginResponse(status: Resource<LoginResponse?>?) {
        loader?.dismiss()
        when (status) {
            is Resource.Success -> { launchIntent(status) }
            is Resource.DataError -> { showError(binding.root,status.errorMsg) }
            else -> {

            }
        }
    }

    private fun launchIntent(response: Resource.Success<LoginResponse?>) {
        sessionManager.run {
            saveAuthToken(response.data?.accessToken?:"")
            saveRefreshToken(response.data?.refreshToken?:"")
        }
        requireActivity().startNormalActivity(HomeActivityMain::class.java)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_login-> {
                hideKeyboard()
                val validation=viewModel.validation(binding.model)
                if(validation.first){
                    loader?.show(requireActivity().supportFragmentManager,"")
                    viewModel.login(binding.model)
                }else {
                    showError(binding.root,validation.second)
                }
            }

            R.id.tv_forgot_username ->{ findNavController().navigate(R.id.action_loginFragment_to_forgotEmailFragment) }
            R.id.tv_forgot_password ->{ findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment) }
        }
    }

    private fun isEnable() {
        if(binding.edtEmail.length()>0 && binding.edtPwd.length()>0) binding.model = LoginModel(enable = true, value = binding.edtEmail.text.toString(), password = binding.edtPwd.text.toString())
        else binding.model = LoginModel(enable = false, value = binding.edtEmail.text.toString(), password = binding.edtPwd.text.toString())
    }
}