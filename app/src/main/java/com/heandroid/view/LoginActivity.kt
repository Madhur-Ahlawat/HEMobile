package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.ViewModelFactory
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.heandroid.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LoginViewModel
    private lateinit var databinding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        sessionManager = SessionManager(this)
        setupViewModel()
        databinding.lifecycleOwner = this
        setupUI()
        //setupObservers()
        databinding.btnLogin.isEnabled= false
        setBtnNormal()
        databinding.edtEmail.doOnTextChanged { text, start, before, count ->
            setBtnActivated()
        }

        databinding.btnLogin.setOnClickListener {

            hideSoftKeyboard()
            val username = databinding.edtEmail.text.toString()
            val pwd = databinding.edtPwd.text.toString()
            if(validate(username, pwd))
            {
               // setupObservers()
                databinding.progressLayout.visibility= View.VISIBLE
                byPassToDashboard()
            }

//            val handler = Handler()
//            handler.postDelayed(object : Runnable {
//                override fun run() {
//                    handler.postDelayed(this, 200)
//
//                }
//            }, 200)
            // getRenewalAccessToken()
        }

        databinding.tvForgotUsername.setOnClickListener {
            var intent = Intent(this, RecoveryUsernamePasswordActivity::class.java)
            startActivity(intent)
        }

        databinding.tvForgotPassword.setOnClickListener {
            var intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        databinding.btnBack.setOnClickListener {
            finish();
        }

    }

    private fun byPassToDashboard() {
        val intent = Intent(this, HomeActivityMain::class.java)
//        val intent = Intent(this, DashboardPage::class.java)
        var bundle = Bundle()
        bundle.putString("access_token", "fbkjbfjk")
        bundle.putString("refresh_token", "jkdhgdf")
        intent.putExtra("data", bundle)
        startActivity(intent)
        finish()

    }

    fun validate(username: String, pwd: String): Boolean {

        return if(username.isEmpty()) {
            showToast(getString(R.string.txt_error_username))
            false
        } else if(pwd.isEmpty()) {
            showToast(getString(R.string.txt_error_password))
            false
        } else {
            true
        }

    }


    private fun setupObservers() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agecyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = databinding.edtEmail.text.toString()
        //var value = "459144698"
        //var password = "Welcome1!"
        var password = databinding.edtEmail.text.toString()
        var validatePasswordCompliance = "true"
        Log.d("DummyLogin", "Before api call")

        viewModel.loginUser(
            clientID,
            grantType,
            agecyId,
            clientSecret,
            value,
            password,
            validatePasswordCompliance
        )
        viewModel.loginUserVal.observe(this,
            {
                when (it.status) {
                    Status.SUCCESS -> {

                        databinding.progressLayout.visibility= GONE
                        var loginResponse = it.data!!.body() as LoginResponse
                        launchDashboardScreen(loginResponse)
                    }

                    Status.ERROR->{
                        databinding.progressLayout.visibility=GONE
                        showToast(it.message)
                    }

                    Status.LOADING->{
                        // show/hide loader
                        databinding.progressLayout.visibility = VISIBLE
                    }
                }
            })

//
//        viewModel.loginUser(clientID,
//            grantType,
//            agecyId,
//            clientSecret,
//            value,
//            password,
//            validatePasswordCompliance)

//            .observe(this, Observer {
//                Log.d("DummyLogin", "after api call")
//                it.let { resource ->
//                    run {
//                        when (resource.status) {
//                            Status.SUCCESS -> {
//                                var loginResponse = resource.data!!.body() as LoginResponse
//                                launchDashboardScreen(loginResponse)
//                            }
//                            Status.ERROR -> {
//                                showToast(resource.message)
//                            }
//
//                            Status.LOADING -> {
//                                // show/hide loader
//                            }
//
//                        }
//                    }
//                }
//            })
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun launchDashboardScreen(loginResponse: LoginResponse) {
        sessionManager.saveAuthToken(loginResponse.accessToken)
        sessionManager.saveRefreshToken(loginResponse.refreshToken)
        Log.d("RefreshTokenFromLOgin", loginResponse.refreshToken)
        var accessToken = loginResponse.accessToken
        val intent = Intent(this, HomeActivityMain::class.java)
//        val intent = Intent(this, DashboardPage::class.java)
        var bundle = Bundle()
        bundle.putString("access_token", accessToken)
        bundle.putString("refresh_token", loginResponse.refreshToken)
        intent.putExtra("data", bundle)
        startActivity(intent)
        finish()

    }

    private fun setupUI() {
       // Toast.makeText(this, "I am launched after api call", Toast.LENGTH_LONG).show()
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        databinding.loginViewModel = viewModel
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun hideSoftKeyboard()
    {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }
    private fun setBtnActivated() {
        databinding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))
        databinding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
        databinding.btnLogin.isEnabled = true
    }

    private fun setBtnNormal() {
        databinding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.color_C9C9C9))
        databinding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.color_7D7D7D))
        databinding.btnLogin.isEnabled = false

    }
}


