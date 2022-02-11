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
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.view.CrossingHistoryActivity
import com.heandroid.view.VehicleMgmtActivity
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory

class VehicleFragment : BaseFragment() {

    private var accessToken: String = ""
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentVehicleBinding
    private lateinit var dashboardViewModel: DashboardViewModel


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
        setupViewModel()
        setupObservers()
    }


    private fun setupObservers() {
        setViews()
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
        dataBinding.vehicleCrossingHistoryLyt.setOnClickListener {
            startActivity(Intent(requireActivity(), CrossingHistoryActivity::class.java))
        }

    }

    private val mList = ArrayList<VehicleResponse>()

    private fun startVehicleMgmtActivity(type: Int) {

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
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun getVehicleListApiCall() {
        dashboardViewModel.getVehicleInformationApi()
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

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()

    }

}