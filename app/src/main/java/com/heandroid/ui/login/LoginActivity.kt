package com.heandroid.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.heandroid.data.model.response.login.LoginResponse
import com.heandroid.databinding.ActivityLoginBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.Resource
import com.heandroid.utils.SessionManager
import com.heandroid.utils.SingleEvent
import com.heandroid.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var sessionManager: SessionManager


    private fun showToastMessage(it: String) {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    override fun initViewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setViewListeners()

        var clientID = "HE_MAPP_NP"
        var grantType = "password"
        var agecyId = "18"
        var clientSecret = "hAvvxMfc25rjkCjJA8sCL4T4yQkfTe"
        var value = "100312942"
        var password = "Welcome1"
        var validatePasswordCompliance = "true"
        Log.d("DummyLogin", "Before api call")
        binding.btnLogin.setOnClickListener {
            loginViewModel.loginUser(
                clientID,
                grantType,
                agecyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance
            )
        }

    }

    private fun setViewListeners() {
        var clientID = "HE_MAPP_NP"
        var grantType = "password"
        var agecyId = "18"
        var clientSecret = "hAvvxMfc25rjkCjJA8sCL4T4yQkfTe"
        var value = "100312942"
        var password = "Welcome12"
        var validatePasswordCompliance = "true"
        Log.d("DummyLogin", "Before api call")
        binding.btnLogin.setOnClickListener {
            loginViewModel.loginUser(
                clientID,
                grantType,
                agecyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance
            )
        }
    }

    private fun handleLoginResponse(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> showLoadingView()
            is Resource.Success -> status.data?.let { showLoginData(response = it) }
            is Resource.DataError -> {
                showDataView(false)
                status.errorMsg?.let { loginViewModel.showToastMessage(errorMsg = it) }
            }
        }
    }

    private fun showDataView(b: Boolean) {
        // hide data view
    }

    private fun showLoginData(response: LoginResponse) {

        // set values in shared preference
        Log.d("data: ", response.accessToken)

        sessionManager.saveAuthToken(response.accessToken)
        sessionManager.saveRefreshToken(response.refreshToken)

    }

    private fun showLoadingView() {
        // show loader
    }


    override fun observeViewModel() {
        observe(loginViewModel.loginUserVal, ::handleLoginResponse)
        observeSnackBarMessages(loginViewModel.showSnackBar)
        observeToast(loginViewModel.showToast)
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        //binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
       // binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

}