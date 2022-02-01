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
import com.heandroid.model.AlertMessage
import com.heandroid.model.AlertMessageApiResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.SessionManager
import com.heandroid.view.HomeActivityMain
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.ViewModelFactory
import java.lang.StringBuilder

class DashboardFragment : BaseFragment() {

    private var accessToken: String=""
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
        accessToken  = (requireActivity() as HomeActivityMain).getAccessToken().toString()
        var urlString =
            "https://mobileapp.sunpass.com/vector/account/home/ftAccountSettings.do?name=sms"
        dataBinding.btnUpdateNow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
        }

        setupViewModel()
        setupObservers()
//        getVehicleListApiCall()

    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setupObservers() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        stringBuilder.append(accessToken)
        //var  token = "Bearer $accessToken"
        var  token = stringBuilder.toString()
        Log.d("token==", token)
        //var token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJBZ2VuY3lJZCI6MTgsIkJyb2tlcklkIjozMTk3LCJ1c2VyX25hbWUiOjMxOTcsIkZpcnN0TmFtZSI6bnVsbCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DTElFTlQiXSwiY2xpZW50X2lkIjoiSEVfTUFQUF9OUCIsImlzUGFzc3dvcmRDb21wbGlhbnQiOmZhbHNlLCJJbnRlcm5hbEFnZW5jeUlkIjowLCJzY29wZSI6WyJ2ZWN0b3JBUEkiLCJmZWF0dXJlSGl0Il0sIlBlcm1pc3Npb24iOm51bGwsIkxhc3ROYW1lIjpudWxsLCJpc1Bhc3N3b3JkRXhwaXJlZCI6ZmFsc2UsImV4cCI6MTY0MjY5MTEwNSwicmVxdWlyZTJGQSI6ZmFsc2UsImp0aSI6ImQ0YTRlNzhiLTAxNDItNDZiZC1iYWQ3LTMwYmQyOGRlYmMyNiJ9.w7ErXODCNXYRqHep4jXr6vtDfbe59ouNFxnhCKMRD7Gch26ZYj8N-ifB3YywpETDxoMgCjnbeIaMn46LkfOy2hb2Yrcaf-CwnOti0k1PJnvsIYi6EQZyC8z11j6yJppmzpsTS7LpInceGH-QmfSNiGtSSYfMrjHWSo9fFPOBV6N00DWq8hxyC-k3HspyNeWOY2NiqF1VOm8TILbidpsPx3UnofRBaDVs_nnhEKuD_TvXU5bKh5cL2XnmHJLEFXKU9k0rNdDdbJupkdvhbu4w1MscUm2C-ryBuZYVSmbJzCQNcmA9yMLIjLgYIt0s39cH0RE9rSPQBiwK_TIJqFatMw"
        if (token != null) {
            var lng = "ENU"
            dashboardViewModel.getAlertsApi(token, lng)

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
        if(item.message.contains("href")) {
            var msgArr = item.message.split("<a")
            var urlString = msgArr[1].split(">")[0].subSequence(7, msgArr[1].split(">")[0].length)
            Log.d("msgArr", msgArr[0])
            Log.d("urlString", urlString.toString())
//            dataBinding.btnUpdateNow.text = msgArr[1].split(">")[1]
//            dataBinding.tvTitle.text = msgArr[0]
        }
        else {
            // do nothing
        }
    }

    private fun getVehicleListApiCall() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        stringBuilder.append(accessToken)
        //var  token = "Bearer $accessToken"
        var  token = stringBuilder.toString()
        Log.d("token==", token)
        dashboardViewModel.getVehicleInformationApi(token)
        dashboardViewModel.vehicleListVal.observe(requireActivity(), androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var vehicleList = resource.data!!.body()
                        if (vehicleList != null) {
                            Log.d("apiResp:getVehicleListApiCall ", vehicleList.size.toString())
                            Log.d("apiResp:getVehicleListApiCall vehicleList.toString() ", vehicleList.toString())
//                            setupVehicleData(vehicleList)
                        }

                    }
                    Status.ERROR -> {
                        Toast.makeText(requireActivity(), resource.message, Toast.LENGTH_LONG).show()

                    }
                    Status.LOADING -> {
                        // show/hide loader
                    }

                }
            }
        })
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity() , message , Toast.LENGTH_SHORT).show()
    }

}