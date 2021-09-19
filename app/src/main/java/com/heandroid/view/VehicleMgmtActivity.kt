package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VehicleListAdapter
import com.heandroid.model.VehicleApiResp
import com.heandroid.utils.Constants
import kotlinx.android.synthetic.main.activity_vehicle_mgmt.*

class VehicleMgmtActivity: AppCompatActivity() {

    private lateinit var vehicleListAdapter:VehicleListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_mgmt)
        setView()
    }

    private fun setView() {
        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        vehicleListAdapter = VehicleListAdapter(this)
        vehicleListAdapter.setList(vehicleResp?.vehicleList)
        rv_vehicle.layoutManager = LinearLayoutManager(this)
        rv_vehicle.setHasFixedSize(true)
        rv_vehicle.adapter = vehicleListAdapter
    }
}