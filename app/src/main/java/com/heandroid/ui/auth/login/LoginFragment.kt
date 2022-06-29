package com.heandroid.ui.auth.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.databinding.FragmentLoginBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.openActivityWithData
import com.heandroid.utils.extn.startNormalActivity
import com.heandroid.utils.extn.toolbar
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

    private fun handleLoginResponse(status: Resource<LoginResponse?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
//                    if ((requireActivity() as AuthActivity).value == Constants.NORMAL_LOGIN_FLOW_CODE) {
                    launchIntent(status)
//                    } else {
//                        launchSubmitComplaint(status)

//                    }
                }
                is Resource.DataError -> {
                    showError(binding.root, status.errorMsg)
                }
                else -> {

                }
            }
        } catch (e: Exception) {
        }
    }

    private fun launchSubmitComplaint(response: Resource.Success<LoginResponse?>) {
        sessionManager.run {
            saveAuthToken(response.data?.accessToken ?: "")
            saveRefreshToken(response.data?.refreshToken ?: "")
            setAccountType(response.data?.accountType ?: Constants.PERSONAL_ACCOUNT)
            isSecondaryUser(response.data?.isSecondary ?: false)

            //saveAccountNumber(response.data?.user_name?:"")
            saveAccountType(response.data?.accountType ?: "")
            setLoggedInUser(true)
        }
        requireActivity().openActivityWithData(ContactDartChargeActivity::class.java) {
            putInt(Constants.FROM_LOGIN_TO_CASES, Constants.FROM_LOGIN_TO_CASES_VALUE)

        }

    }

    private fun launchIntent(response: Resource.Success<LoginResponse?>) {
        sessionManager.run {
            saveAuthToken(response.data?.accessToken ?: "")
            saveRefreshToken(response.data?.refreshToken ?: "")
            setAccountType(response.data?.accountType ?: Constants.PERSONAL_ACCOUNT)
            isSecondaryUser(response.data?.isSecondary ?: false)
            //saveAccountNumber(response.data?.user_name?:"")
            saveAccountType(response.data?.accountType ?: "")
            setLoggedInUser(true)

        }
        requireActivity().startNormalActivity(HomeActivityMain::class.java)
/*
            if ((requireActivity() as AuthActivity).value == Constants.NORMAL_LOGIN_FLOW_CODE) {

                requireActivity().startNormalActivity(HomeActivityMain::class.java)
            } else {
                requireActivity().openActivityWithData(ContactDartChargeActivity::class.java) {
                    putInt(
                        Constants.FROM_LOGIN_TO_CASES,
                        Constants.FROM_LOGIN_TO_CASES_VALUE
                    )
                    putString(Constants.LAST_NAME,response.data?.user_name?:"")
                }

            }
*/
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                hideKeyboard()
                loader?.show(requireActivity().supportFragmentManager, "")
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


