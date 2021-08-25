package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
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
        }

    }

//   fun onLoginClick(view:View)
//   {
//       setupObservers()
//   }

    private fun setupObservers() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agecyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"
        Log.d("DummyLogin", "Before api call")
        viewModel.loginUser( clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
            .observe(this , Observer {
                Log.d("DummyLogin", "after api call")
                it.let {
                        resource ->
                    run {
                        when (resource.status) {
                            Status.SUCCESS -> {
                                var loginResponse = resource.data!!.body() as LoginResponse
                                launchDashboardScreen(loginResponse)
                            }
                            Status.ERROR->{
                                showToast(resource.message)}

                            Status.LOADING->{
                                // show/hide loader
                            }

                        }
                    }
                }
            })
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    private fun launchDashboardScreen(loginResponse: LoginResponse) {
        sessionManager.saveAuthToken(loginResponse.accessToken)
        sessionManager.saveRefrehToken(loginResponse.refreshToken)
        var accessToken = loginResponse.accessToken
        val intent = Intent(this, DashboardPage::class.java)
        var bundle = Bundle()
        bundle.putString("access_token", accessToken)
        intent.putExtra("data", bundle)
        startActivity(intent)

    }
    private fun setupUI() {
        Toast.makeText(this, "I am launched after api call", Toast.LENGTH_LONG).show()
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelper(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

    }

}


