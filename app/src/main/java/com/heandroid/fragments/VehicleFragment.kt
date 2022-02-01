package com.heandroid.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.FragmentVehicleBinding
import com.heandroid.model.PlateInfoResponse
import com.heandroid.model.VehicleApiResp
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.view.HomeActivityMain
import com.heandroid.view.VehicleMgmtActivity
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.ViewModelFactory
import java.lang.StringBuilder

class VehicleFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentVehicleBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var accessToken: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_vehicle,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = (requireActivity() as HomeActivityMain).getAccessToken().toString()

        setViews()
        setupViewModel()
        getVehicleListApiCall()
    }

    private fun setViews() {

        dataBinding.vehicleListLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_LIST)
        }

        dataBinding.addVehicleLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_ADD)
        }
        dataBinding.vehicleHistoryLyt.setOnClickListener {
            startVehicleMgmtActivity(Constants.VEHICLE_SCREEN_TYPE_HISTORY)

        }

    }

    private val mList = ArrayList<VehicleResponse>()

    private fun startVehicleMgmtActivity(type: Int) {

/*
        val plateInfoResp = PlateInfoResponse("DFH 6444", "UK", "HE", "-", "", "", "")
        val vehicleInfoResp = VehicleInfoResponse(
            "Aadi",
            "TT FI",
            "2019",
            "",
            "1_GVVKGV",
            "",
            "Red",
            "B",
            "23 Aug 2022"
        )

        val mVehicleResponse1 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse2 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse3 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse4 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        mList.add(mVehicleResponse1)
        mList.add(mVehicleResponse2)
        mList.add(mVehicleResponse3)
        mList.add(mVehicleResponse4)
*/
        if (mList.size > 0) {
            var vehicleApiResp = VehicleApiResp(mList)
            var bundle = Bundle()
            bundle.putSerializable(Constants.VEHICLE_RESPONSE, vehicleApiResp)
            var intent = Intent(requireActivity(), VehicleMgmtActivity::class.java)
            intent.putExtra(Constants.VEHICLE_DATA, bundle)
            intent.putExtra(Constants.VEHICLE_SCREEN_KEY, type)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun getVehicleListApiCall() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        stringBuilder.append(accessToken)
        //var  token = "Bearer $accessToken"
        var token = stringBuilder.toString()
        Log.d("token==", token)
        dashboardViewModel.getVehicleInformationApi(token)
        dashboardViewModel.vehicleListVal.observe(requireActivity(), androidx.lifecycle.Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var vehicleList = resource.data!!.body()
                        if (vehicleList != null) {
                            mList.clear()
                            mList.addAll(vehicleList)
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


}