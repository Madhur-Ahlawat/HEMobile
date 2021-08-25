package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.heandroid.R
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiClient
import com.heandroid.network.ApiHelper
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.AppRepository
import com.heandroid.utils.Resource
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.MainViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*


class DummyLoginActivity: AppCompatActivity()
{

//    lateinit var loginViewModel: LoginViewModel
//    private lateinit var sessionManager: SessionManager
//    private lateinit var apiClient: ApiClient
//
//    lateinit var tvForgotPassword: TextView
//    lateinit var tfUserName: TextInputEditText
//    lateinit var tfPassword: TextInputEditText
//    lateinit var btnSignIn: TextView
//    lateinit var tvCreateAccount: TextView
//    var userName: String = ""
//    var password: String = ""
//    var LOGIN_TAG: String = "Login Screen::"
//    var accessToken: String = ""
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        init()
//        sessionManager = SessionManager(this)
//
//        tv_forgot_password.setOnClickListener {
//            Log.d("TOKEN VALUE", "accessToken====$accessToken")
//        }
//
//    }
//
//
//    private fun init() {
//        val repository = AppRepository()
//        val factory = ViewModelProviderFactory(application, repository)
//        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
//    }
//
//    fun onLoginClick(view: View) {
//        var uName = edt_username.text.toString()
//        var uPwd = edt_password.text.toString()
//
//        var clientID = "NY_EZ_Pass_iOS_QA"
//        var grantType = "password"
//        var agecyId = "12"
//        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
//        var value = "johnsmith32"
//        var password = "Welcome1!"
//        var validatePasswordCompliance = "true"
//
//        val map: HashMap<String, String> = HashMap()
//        map.put("clientID", "NY_EZ_Pass_iOS_QA");
//        map.put("grantType", "password");
//        map.put("agecyId", "12");
//        map.put("clientSecret", "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7");
//        map.put("value", "johnsmith");
//        map.put("password", "Welcome1!");
//        map.put("validatePasswordCompliance", "true");
//
//
//        loginViewModel.loginUser(
//            clientID, grantType, agecyId, clientSecret, value, password, validatePasswordCompliance)
//
//        loginViewModel.loginResponse.observe(this, Observer { event ->
//            event.getContentIfNotHandled()?.let { response ->
//                when (response) {
//                    is Resource.Success -> {
//                        Log.d("Req Success", ""+response)
//                        launchDashboardScreen(response.data as LoginResponse)
//                    }
//
//                    is Resource.Error -> {
//                        Log.d("Req Success", "")
//                        // Error logging in
//                        Toast.makeText(this@LoginActivity,
//                            "Please check your login credentials.",
//                            Toast.LENGTH_LONG).show()
//                    }
//
//                    else ->
//                    {
//                        // do nothing
//                    }
//                }
//            }
//        })
//    }
//
//    private fun launchDashboardScreen(loginResponse: LoginResponse) {
//        sessionManager.saveAuthToken(loginResponse.accessToken)
//        sessionManager.saveRefrehToken(loginResponse.refreshToken)
//        accessToken = loginResponse.accessToken
//        val intent = Intent(this@LoginActivity, DashboardPage::class.java)
//        var bundle = Bundle()
//        bundle.putString("access_token", accessToken)
//        intent.putExtra("data", bundle)
//        startActivity(intent)
//
//    }


    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        setupViewModel()
        setupUI()
        setupObservers()
    }

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
                              var loginResponse = resource.data as LoginResponse
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
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

    }

}

