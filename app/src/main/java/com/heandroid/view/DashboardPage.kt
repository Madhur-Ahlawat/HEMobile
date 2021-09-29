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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heandroid.network.ApiClient
import com.heandroid.R
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.DummyTestViewModel
import com.heandroid.viewmodel.LoginViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_dashborad.*


class DashboardPage : AppCompatActivity() {
    private var monthlyUsageApiResp: RetrievePaymentListApiResponse?=null
    private var paymentListApiResponse: RetrievePaymentListApiResponse? = null
    private var vehicleList: List<VehicleResponse> = mutableListOf()
    private lateinit var dummyViewModel: DummyTestViewModel
    private var refreshToken: String?=null
    private var accessToken: String? =  null
    private var ACCOUNT_TAG = "Account Screen"
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashborad)
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
        progress_layout.visibility= View.VISIBLE
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)

            }
        }, 200)

        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

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
            .observe(this, androidx.lifecycle.Observer {
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

        var requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        viewModel.retrievePaymentListApi("Bearer $accessToken" , requestParam)
            .observe(this, androidx.lifecycle.Observer {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            paymentListApiResponse = resource.data!!.body()
                            Log.d("paymentResp:retrievePaymentListApi:: " , paymentListApiResponse.toString())
                            Log.d("added for pipeline", "true")

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
        tv_vehicle_count.text = vehicleList.size.toString()
        tv_remaining_amount.text = vehicleList.size.toString()


    }

    private fun getAccountOverViewApi(accessToken:String)
    {
        viewModel.getAccountOverViewApi("Bearer $accessToken")
        viewModel.accountOverviewVal.observe(this, androidx.lifecycle.Observer {
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

//        dummyViewModel.fetchAccountOverview("Bearer $accessToken")
//        dummyViewModel.getAccountOverView().observe(this , Observer {
//            when(it.status)
//            {
//                Status.SUCCESS->{
//                    var accountResponse = it.data?.body()
//                    accountResponse?.accountInformation?.type?.let { it1 ->
//                        Log.d("AccountApiResp: " ,
//                            it1
//                        )
//                    }
//                    setView(accountResponse)
//
//                }
//                Status.ERROR->{
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                }
//                Status.LOADING->{
//                    // show/hide progress bar
//                }
//            }
//        })
    }
    private fun setupUI() {
        tv_vehicle_heading.setOnClickListener {
            startVehicleMgmtActivity()
        }

        tv_monthly_usage_heading.setOnClickListener {
            startMonthlyUsageActivity()
        }

        tv_payment_heading.setOnClickListener {
            startPaymentHistoryActivity()
        }
    }

    private fun startMonthlyUsageActivity() {
        var bundle = Bundle()
        bundle.putSerializable(Constants.PAYMENT_RESPONSE , monthlyUsageApiResp )
        var intent = Intent(this, ActivityPaymentHistory::class.java)
        intent.putExtra(Constants.PAYMENT_DATA, bundle)
        startActivity(intent)
        finish()
    }

    private fun startPaymentHistoryActivity() {
        var bundle = Bundle()
        bundle.putSerializable(Constants.PAYMENT_RESPONSE , paymentListApiResponse )
        var intent = Intent(this, ActivityPaymentHistory::class.java)
        intent.putExtra(Constants.PAYMENT_DATA, bundle)
        startActivity(intent)
        finish()

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
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        dummyViewModel = ViewModelProvider(this,factory)[DummyTestViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setView(accountResponse: AccountResponse?) {
        if (accountResponse != null) {
            tv_available_balance.text =
                "${getString(R.string.txt_pound)}${accountResponse.financialInformation.currentBalance}"
            //tv_remaining_amount.text =
             //   "${getString(R.string.txt_pound)}${accountResponse.financialInformation.tollBalance}"
            account_number_id.text =
                "${"Account Number :"}${accountResponse.accountInformation.number}"
            tv_pre_pay_account_heading.text=
                "${accountResponse.accountInformation.type}"
            tv_account_number.text=
                "${accountResponse.accountInformation.number}"
            accountStatus_id.text =
                "${"Account Status :"}${accountResponse.accountInformation.status}"
            accountType_id.text =
                "${"Account Type :"}${accountResponse.accountInformation.type}"
            topUp_id.text =
                "${"Top up Type :"}${accountResponse.financialInformation.financialStatus}"
            tv_manual_top_up.text = accountResponse.financialInformation.financialStatus
        }
        progress_layout.visibility= View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

}