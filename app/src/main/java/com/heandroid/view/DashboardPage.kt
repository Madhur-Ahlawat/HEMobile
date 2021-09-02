package com.heandroid.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.network.ApiClient
import com.heandroid.R
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardPage : AppCompatActivity() {
    private var refreshToken: String?=null
    private var accessToken: String? =  null
    private var ACCOUNT_TAG = "Account Screen"
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)
        apiClient = ApiClient()
        val token: String = SessionManager.USER_TOKEN
        Log.d("DashBoard Page ::token", token)
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
                getRetrievePaymentListApiCall(accessToken!!)
                getMonthlyUsageApiCall(accessToken!!)

            }
             refreshToken = it.getString("refresh_token")
            if(refreshToken!=null)
            {
                getRenewalAccessToken()
            }

        }

    }

    private fun getMonthlyUsageApiCall(accessToken: String) {

        var requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "12/03/2021",
            "01/09/2021",
            "PREPAID",
            "TX_DATE",
            0,10,"123456789",
            "ABC123QW"
        )
        viewModel.getMonthlyUsage("Bearer $accessToken" , requestParam)
            .observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var monthlyUsageApiResp = resource.data!!.body()
                            Log.d("resp: " , monthlyUsageApiResp.toString())
                            setupMonthlyUsageView(monthlyUsageApiResp)

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
    private fun getRetrievePaymentListApiCall(accessToken: String) {

        var requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "12/03/2021",
            "01/09/2021",
            "PREPAID",
            "TX_DATE",
            0,10,"123456789",
            "string"
        )
        viewModel.retrievePaymentListApi("Bearer $accessToken" , requestParam)
            .observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var paymentListApiResponse = resource.data!!.body()
                            Log.d("resp: " , paymentListApiResponse.toString())

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

    private fun setupMonthlyUsageView(crossingListResp: RetrievePaymentListApiResponse?) {
        if (crossingListResp != null) {
            tv_crossing_count.text = crossingListResp.count.toString()
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
                            Log.d("Dash Board Page:: Account Response ::",accountResponse.toString())
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
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setView(accountResponse: AccountResponse?) {
        if (accountResponse != null) {
            tv_available_balance.text =
                "${getString(R.string.txt_euro)}${accountResponse.financialInformation.currentBalance}"
            tv_remaining_amount.text =
                "${getString(R.string.txt_euro)}${accountResponse.financialInformation.tollBalance}"
            account_number_id.text =
                "${"Account Number :"}${accountResponse.accountInformation.number}"
            tv_account_number.text=
                "${accountResponse.accountInformation.number}"
            accountStatus_id.text =
                "${"Account Status :"}${accountResponse.accountInformation.status}"
            accountType_id.text =
                "${"Account Type :"}${accountResponse.accountInformation.type}"
            topUp_id.text =
                "${"Topup Balance :"}${accountResponse.accountInformation.openViolationCount}"
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


    private fun getRenewalAccessToken()
    {
        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken1 = sessionManager.fetchRefreshToken()
        Log.d("RefreshToken", refreshToken!!)
        var validatePasswordCompliance =  "true"
        Log.d("RenewalAccessToken", "Before api call")
        if (refreshToken1 != null) {
            loginViewModel.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret,
                refreshToken1!!, validatePasswordCompliance).
            observe(this, Observer {
                Log.d("RenewalAccessToken", "after api call")
                it.let {
                    resource ->
                    run {
                        when (resource.status) {
                            Status.SUCCESS -> {
                                Log.d("FinalResp: ", resource.data.toString())
                                var loginResponse = resource.data!!.body() as LoginResponse
                                Log.d("renewalResp: ", loginResponse.statusCode.toString())

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


    }

    private fun showToast(message: String?) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}