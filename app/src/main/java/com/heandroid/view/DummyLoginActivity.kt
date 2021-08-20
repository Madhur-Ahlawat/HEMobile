package com.heandroid.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.network.ApiHelper
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.AppRepository
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.MainViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.viewmodel.ViewModelProviderFactory

class DummyLoginActivity: AppCompatActivity()
{
private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

                {
                    Log.d("Mindorks", it.data.toString())

                    }
                }
            })
    }

    private fun setupUI() {
       Toast.makeText(this, "I am launmched after api call", Toast.LENGTH_LONG).show()
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelper(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

    }

}

