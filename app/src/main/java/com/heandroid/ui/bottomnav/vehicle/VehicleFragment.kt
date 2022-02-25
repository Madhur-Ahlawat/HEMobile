package com.heandroid.ui.bottomnav.vehicle

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
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.databinding.FragmentVehicleBinding
import com.heandroid.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleFragment : BaseFragment<FragmentVehicleBinding>() {

    private var accessToken: String = ""
//    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentVehicleBinding
//    private lateinit var dashboardViewModel: DashboardViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*  setupViewModel()
          setupObservers()
  */
    }


/*
    private fun setupObservers() {
        setViews()
        getVehicleListApiCall()
    }
*/

/*
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
*/

    /*  private val mList = ArrayList<VehicleResponse>()

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
  */
/*
    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }
*/

/*
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
*/



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleBinding = FragmentVehicleBinding.inflate(inflater, container, false)


    override fun init() {
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}