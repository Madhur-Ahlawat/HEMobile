package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        setupViewModel()
        setupUI()
        //setupObservers()
        btn_login.setOnClickListener {
            setupObservers()
            // getRenewalAccessToken()
        }

    }


    private fun setupObservers() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agecyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = edt_username.text.toString()
        //var value = "459144698"
        //var password = "Welcome1!"
        var password = edt_password.text.toString()
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
                        var loginResponse = it.data!!.body() as LoginResponse
                        launchDashboardScreen(loginResponse)
                    }

                    Status.ERROR->{
                        showToast(it.message)
                    }

                    Status.LOADING->{
                        // show/hide loader
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
        sessionManager.saveRefrehToken(loginResponse.refreshToken)
        Log.d("RefreshTokenFromLOgin", loginResponse.refreshToken)
        var accessToken = loginResponse.accessToken
        val intent = Intent(this, DashboardPage::class.java)
        var bundle = Bundle()
        bundle.putString("access_token", accessToken)
        bundle.putString("refresh_token", loginResponse.refreshToken)
        intent.putExtra("data", bundle)
        startActivity(intent)

    }

    private fun setupUI() {
        Toast.makeText(this, "I am launched after api call", Toast.LENGTH_LONG).show()
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun getRenewalAccessToken() {
//        formData.append("client_id", environment.clientId);
//        formData.append("grant_type", "refresh_token");
//        formData.append("agencyID", environment.agencyId);
//        formData.append("client_secret", environment.clientSecret);
//        formData.append("refresh_token", refreshToken);
//        formData.append("validatePasswordCompliance", "true");

        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = sessionManager.fetchRefreshToken()
        var validatePasswordCompliance = "true"
        Log.d("RenewalAccessToken", "Before api call")
        if (refreshToken != null) {
            viewModel.getRenewalAccessToken(
                clientId,
                grantType,
                agencyId,
                clientSecret,
                refreshToken,
                validatePasswordCompliance
            ).observe(this, Observer {
                Log.d("RenewalAccessToken", "after api call")
                it.let { resource ->
                    run {
                        when (resource.status) {
                            Status.SUCCESS -> {
                                var loginResponse = resource.data!!.body() as LoginResponse
                                //launchDashboardScreen(loginResponse)
                            }
                            Status.ERROR -> {
                                showToast(resource.message)
                            }

                            Status.LOADING -> {
                                // show/hide loader
                            }

                        }
                    }
                }
            })
        }


    }


}


