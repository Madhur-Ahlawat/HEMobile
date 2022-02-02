package com.heandroid.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.FragmentDashboardBinding
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.view.HomeActivityMain
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import java.lang.StringBuilder

class DashboardFragment : BaseFragment() {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private var accessToken: String = ""
    private lateinit var dataBinding: FragmentDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //accessToken  = SessionManager(requireContext()).fetchAuthToken().toString();
        accessToken = (requireActivity() as HomeActivityMain).getAccessToken().toString()
        var urlString =
            "https://mobileapp.sunpass.com/vector/account/home/ftAccountSettings.do?name=sms"
        dataBinding.btnUpdateNow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
        }

        setupViewModel()
        getVehicleListApiCall()
        setupObservers()

    }

    private fun setupObservers() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        stringBuilder.append(accessToken)
        //var  token = "Bearer $accessToken"
        var token = stringBuilder.toString()
        Log.d("token==", token)
        if (token != null) {
            var lng = "ENU"
            dashboardViewModel.getAlertsApi(lng)

            dashboardViewModel.getAlertsVal.observe(requireActivity(),
                {
                    when (it.status) {
                        Status.SUCCESS -> {

                            var apiResponse = it.data!!.body() as AlertMessageApiResponse
                            Log.d("ApiSuccess : ", apiResponse.messageList.size.toString())
                            setUpNotificationView(apiResponse.messageList)
                        }

                        Status.ERROR -> {
                            showToast(it.message)
                        }

                        Status.LOADING -> {
                            // show/hide loader
                            Log.d("GetAlert: ", "Data loading")
                        }
                    }
                })
        }
    }

    private fun setUpNotificationView(messageList: List<AlertMessage>) {

        var item = messageList[0]
        if (item.message.contains("href")) {
            var msgArr = item.message.split("<a")
            var urlString = msgArr[1].split(">")[0].subSequence(7, msgArr[1].split(">")[0].length)
            Log.d("msgArr", msgArr[0])
            Log.d("urlString", urlString.toString())
//            dataBinding.btnUpdateNow.text = msgArr[1].split(">")[1]
//            dataBinding.tvTitle.text = msgArr[0]
        } else {
            // do nothing
        }
    }

    private fun getVehicleListApiCall() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        dashboardViewModel.getVehicleInformationApi()
        dashboardViewModel.vehicleListVal.observe(requireActivity(), androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var vehicleList = resource.data!!.body()
                        if (vehicleList != null) {
                            Log.d("apiResp:getVehicleListApiCall ", vehicleList.size.toString())
                            Log.d(
                                "apiResp:getVehicleListApiCall vehicleList.toString() ",
                                vehicleList.toString()
                            )
//                            setupVehicleData(vehicleList)
                        }

                    }
                    Status.ERROR -> {
                        Toast.makeText(requireActivity(), resource.message, Toast.LENGTH_LONG)
                            .show()

                    }
                    Status.LOADING -> {
                        // show/hide loader
                    }

                }
            }
        })
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }


    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }


}