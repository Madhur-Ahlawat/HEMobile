package com.conduent.nationalhighways.ui.auth.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adobe.marketing.mobile.MobileCore
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.forgot.email.LoginModel
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.databinding.FragmentLoginBinding
import com.conduent.nationalhighways.ui.base.BaseApplication
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModels()
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_log_in_dart_system))
        BaseApplication.INSTANCE?.let {
            MobileCore.setApplication(it)
            MobileCore.lifecycleStart(null)
        }
    }

    override fun init() {
        binding.model = LoginModel(value = "", password = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {
        binding.apply {
            tvForgotUsername.setOnClickListener(this@LoginFragment)
            tvForgotPassword.setOnClickListener(this@LoginFragment)
            edtEmail.doAfterTextChanged { checkButton() }
            edtPwd.doAfterTextChanged { checkButton() }
            btnLogin.setOnClickListener(this@LoginFragment)
        }
    }

    override fun observer() {
        observe(viewModel.login, ::handleLoginResponse)
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }

    private fun handleLoginResponse(status: Resource<LoginResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                launchIntent(status)
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun launchIntent(response: Resource.Success<LoginResponse?>) {
        sessionManager.run {
            saveAuthToken(response.data?.accessToken ?: "")
            saveRefreshToken(response.data?.refreshToken ?: "")
            setAccountType(response.data?.accountType ?: Constants.PERSONAL_ACCOUNT)
            isSecondaryUser(response.data?.isSecondary ?: false)
            saveAuthTokenTimeOut(response.data?.expiresIn ?: 0)
            saveAccountType(response.data?.accountType ?: "")
            setLoggedInUser(true)
        }
        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                hideKeyboard()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.login(binding.model)

            }

            R.id.tv_forgot_username -> {
                findNavController().navigate(R.id.action_loginFragment_to_forgotEmailFragment)
            }
            R.id.tv_forgot_password -> {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
    }

    private fun checkButton() {
        if (binding.edtEmail.length() > 0 && binding.edtPwd.length() > 0) {
            binding.model = LoginModel(
                value = binding.edtEmail.text.toString(),
                password = binding.edtPwd.text.toString(),
                enable = true
            )
        } else {
            binding.model = LoginModel(
                value = binding.edtEmail.text.toString(),
                password = binding.edtPwd.text.toString(),
                enable = false
            )
        }
    }

}


