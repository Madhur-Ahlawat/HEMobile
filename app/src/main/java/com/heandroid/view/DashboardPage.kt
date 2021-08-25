package com.heandroid.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.heandroid.network.ApiClient
import com.heandroid.R
import com.heandroid.model.AccountResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.network.ApiHelper
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardPage : AppCompatActivity() {
    private var accessToken: String? =  null
    lateinit var tokenString: String
    private var ACCOUNT_TAG = "Account Screen"
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)
        apiClient = ApiClient()
        tokenString = findViewById<TextView>(R.id.token_id).toString()
        val token: String = SessionManager.USER_TOKEN
        tokenString = token
        Log.d("DashBoard Page ::token", token)
        Log.d("DashBoard Page ::", tokenString)
        sessionManager = SessionManager(this)

//        tvAvailableAmount = findViewById(R.id.tv_available_balance)
//        tvRemainingAmount = findViewById(R.id.tv_remaining_amount)

        sessionManager.fetchAuthToken()?.let {
            //requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d("DashBoard Page ::fetchAuthToken", it)
            //callApiForAccountOverview("Bearer $it")
        }

        setupViewModel()
        setupUI()
        setupObservers()

    }

    private fun setupObservers() {
        var bundle = intent.getBundleExtra("data")
        bundle?.let {
             accessToken = it.getString("access_token")
            if (accessToken != null) {
                Log.d("DashBoard Page ::fetchAuthToken", accessToken!!)
                //callApiForAccountOverview("Bearer $accessToken")
               getAccountOverViewApi(accessToken!!)
                getVehicleListApiCall(accessToken!!)
            }

        }

    }

    private fun getVehicleListApiCall(accessToken: String) {
        viewModel.getVehicleInformationApi("Bearer $accessToken")
            .observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var vehicleList = resource.data!!.body()
                            if (vehicleList != null) {
                                Log.d("apiResp: ", vehicleList.size.toString())
                                setupVehicleData(vehicleList)
                            }

                        }
                        Status.ERROR -> {
                            Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()

                        }
                        Status.LOADING -> {
                            // show/hide loader
                        }

                    }
                }
            })
    }

    private fun setupVehicleData(vehicleList: List<VehicleResponse>) {
        tv_vehicle_count.text = vehicleList.size.toString()

    }

    private fun getAccountOverViewApi(accessToken:String)
    {
        viewModel.getAccountOverViewApi("Bearer $accessToken")
            .observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var accountResponse = resource.data!!.body() as AccountResponse
                            setView(accountResponse)
                        }
                        Status.ERROR -> {
                            Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()

                        }
                        Status.LOADING -> {
                            // show/hide loader
                        }

                    }
                }
            })
    }
    private fun setupUI() {

    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelper(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
    }

    private fun setView(accountResponse: AccountResponse?) {
        if (accountResponse != null) {
            tv_available_balance.text =
                "${getString(R.string.txt_euro)}${accountResponse.financialInformation.currentBalance}"
            tv_remaining_amount.text =
                "${getString(R.string.txt_euro)}${accountResponse.financialInformation.currentBalance}"
        }
    }




    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}