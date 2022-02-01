package com.heandroid.fragments

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
import com.heandroid.view.HomeActivityMain
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import java.lang.StringBuilder

class VehicleFragment : BaseFragment() {

    private var accessToken: String=""
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentVehicleBinding

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
        accessToken  = (requireActivity() as HomeActivityMain).getAccessToken().toString()
        setupViewModel()
        setupObservers()

    }


    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setupObservers() {

        var stringBuilder = StringBuilder()
        stringBuilder.append("Bearer ")
        stringBuilder.append(accessToken)
        //var  token = "Bearer $accessToken"
        var  token = stringBuilder.toString()
        Log.d("token==", token)
        if (token != null) {
            var request  = VehicleResponse(PlateInfoResponse(), VehicleInfoResponse())
            vehicleMgmtViewModel.addVehicleApi(request);
            vehicleMgmtViewModel.addVehicleApiVal.observe(requireActivity(),
                {
                    when (it.status) {
                        Status.SUCCESS -> {

                            var apiResponse = it.data!!.body() as EmptyApiResponse
                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
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

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message , Toast.LENGTH_SHORT).show()
    }


}