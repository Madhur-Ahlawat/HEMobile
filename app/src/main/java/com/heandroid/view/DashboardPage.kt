package com.heandroid.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.ActivityDashboradBinding
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.ViewModelFactory



class DashboardPage : AppCompatActivity() {
    private var monthlyUsageApiResp: RetrievePaymentListApiResponse?=null
    private var paymentListApiResponse: RetrievePaymentListApiResponse? = null
    private var vehicleList: List<VehicleResponse> = mutableListOf()
    private var refreshToken: String?=null
    private var accessToken: String? =  null
    private var ACCOUNT_TAG = "Account Screen"
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataBinding:ActivityDashboradBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_dashborad)
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
        //setupObservers()
        dataBinding.progressLayout.visibility= View.VISIBLE
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)

            }
        }, 200)

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
                //getRenewalAccessToken()
            }

        }

    }

    private fun getMonthlyUsageApiCall(accessToken: String) {

        var requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "06/30/2021",
            "08/31/2021",
            "PREPAID",
            "TX_DATE",
            0,
            10,
            "",
            ""
        )
        viewModel.getMonthlyUsage("Bearer $accessToken" , requestParam)
            viewModel.monthlyUsageVal.observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            monthlyUsageApiResp = resource.data!!.body()
                            Log.d("MonthlyUsageApiresp: " , monthlyUsageApiResp.toString())
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
//        {
//
//            "searchDateType": "Posted Date",
//
//            "startDate": "06/30/2021",
//
//            "endDate": "08/31/2021",
//
//            "transactionType": "PREPAID",
//
//            "sortColumn": "TX_DATE",
//
//            "startIndex": 0,
//
//            "numberOfResults": 10,
//
//            "tagNumber": "",
//
//            "licenseNumber": ""
//
//        }

        var requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "06/30/2021",
            "08/31/2021",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        viewModel.retrievePaymentListApi("Bearer $accessToken" , requestParam)
           viewModel.paymentListVal.observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            paymentListApiResponse = resource.data!!.body()
                            Log.d("paymentResp:retrievePaymentListApi:: " , paymentListApiResponse.toString())
                            Log.d("added for pipeline", "true")
                            setPaymentHistoryView()

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

    private fun setPaymentHistoryView() {

        dataBinding.tvAvailableBalance.text = paymentListApiResponse?.count.toString()
    }

    private fun setupMonthlyUsageView(crossingListResp: RetrievePaymentListApiResponse?) {
        if (crossingListResp != null) {
            dataBinding.tvCrossingCount.text = crossingListResp.count.toString()
        }
    }

    private fun getVehicleListApiCall(accessToken: String) {
        viewModel.getVehicleInformationApi("Bearer $accessToken")
        viewModel.vehicleListVal.observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var vehicleList = resource.data!!.body()
                            if (vehicleList != null) {
                                Log.d("apiResp:getVehicleListApiCall ", vehicleList.size.toString())
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

    private fun setupVehicleData(list: List<VehicleResponse>) {
        vehicleList =  list
        dataBinding.tvVehicleCount.text = vehicleList.size.toString()
        dataBinding.tvAvailableBalance.text = vehicleList.size.toString()


    }

    private fun getAccountOverViewApi(accessToken:String)
    {
        viewModel.getAccountOverViewApi("Bearer $accessToken")
        viewModel.accountOverviewVal.observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var accountResponse = resource.data!!.body() as AccountResponse
                            Log.d("Dashboard Page:: Account Response ::",accountResponse.toString())
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
        dataBinding.tvVehicleHeading.setOnClickListener {
            startVehicleMgmtActivity()
        }

    }

    private fun startMonthlyUsageActivity() {
        var bundle = Bundle()
        bundle.putSerializable(Constants.PAYMENT_RESPONSE , monthlyUsageApiResp )
        var intent = Intent(this, ActivityPaymentHistory::class.java)
        intent.putExtra(Constants.PAYMENT_DATA, bundle)
        startActivity(intent)
        //finish()
    }

    private fun startPaymentHistoryActivity() {

            var bundle = Bundle()
            bundle.putSerializable(Constants.PAYMENT_RESPONSE , paymentListApiResponse )
            var intent = Intent(this, ActivityPaymentHistory::class.java)
            intent.putExtra(Constants.PAYMENT_DATA, bundle)
            startActivity(intent)
            // finish()



    }

    private fun startVehicleMgmtActivity() {
        var vehicleApiResp = VehicleApiResp(vehicleList)
        var bundle = Bundle()
        bundle.putSerializable(Constants.VEHICLE_RESPONSE , vehicleApiResp )
        var intent = Intent(this, VehicleMgmtActivity::class.java)
        intent.putExtra(Constants.VEHICLE_DATA, bundle)
        startActivity(intent)
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setView(accountResponse: AccountResponse?) {
        if (accountResponse != null) {
            dataBinding.tvAvailableBalance.text =
                "${getString(R.string.txt_pound)}${accountResponse.financialInformation.currentBalance}"
            dataBinding.tvAccountStatus.text =
                "${"Account Status: "}${accountResponse.accountInformation.status}"
        }
        dataBinding.progressLayout.visibility= View.GONE
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
                refreshToken1!!, validatePasswordCompliance)
            loginViewModel.renewalUserLoginVal.observe(this, Observer {
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

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

}