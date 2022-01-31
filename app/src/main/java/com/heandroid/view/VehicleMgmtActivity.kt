package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VehicleListAdapter
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.model.VehicleApiResp
import com.heandroid.utils.Constants

class VehicleMgmtActivity: AppCompatActivity() {

    private lateinit var vehicleListAdapter:VehicleListAdapter
    private lateinit var databinding : ActivityVehicleMgmtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this , R.layout.activity_vehicle_mgmt)
        setView()
    }

    private fun setView() {

        databinding.tvBack.setOnClickListener {
            finish()
        }


        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        databinding.tvVehicleCount.text = vehicleResp.vehicleList.size.toString()
        vehicleListAdapter = VehicleListAdapter(this)
        vehicleListAdapter.setList(vehicleResp?.vehicleList)
        databinding.rvVehicle.layoutManager = LinearLayoutManager(this)
        databinding.rvVehicle.setHasFixedSize(true)
        databinding.rvVehicle.adapter = vehicleListAdapter
    }
}