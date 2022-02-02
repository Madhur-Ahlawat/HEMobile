package com.heandroid.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.heandroid.R
import com.heandroid.adapter.VehicleListAdapter
import com.heandroid.adapter.VrmHeaderAdapter
import com.heandroid.adapter.VrmHistoryAdapter
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.dialog.AddVehicle
import com.heandroid.listener.AddVehicleListener
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleMgmtActivity : AppCompatActivity(), AddVehicleListener {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var vehicleListAdapter: VehicleListAdapter
    private lateinit var databinding: ActivityVehicleMgmtBinding
    private lateinit var mAdapter: VrmHeaderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_mgmt)
        setView()
        setupViewModel()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")

    }

    private var mType: Int? = null
    private fun setView() {

        mType = intent?.getIntExtra(Constants.VEHICLE_SCREEN_KEY, 0)

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    databinding.idToolBarLyt.title_txt.text = "Vehicles list"
                    setListAdapter()

                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    databinding.idToolBarLyt.title_txt.text = "Add vehicles"
                    databinding.addVehiclesTxt.text = "Add vehicles to your account"
                    databinding.conformBtn.text = "Add Vehicle"
                    databinding.conformBtn.icon = AppCompatResources.getDrawable(
                        this@VehicleMgmtActivity,
                        R.drawable.ic_baseline_add_24
                    )
                    databinding.conformBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    databinding.conformBtn.iconTint = ColorStateList.valueOf(Color.WHITE)

                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    databinding.idToolBarLyt.title_txt.text = "Vehicles History"
                    databinding.conformBtn.visibility = View.GONE
                    setHistoryAdapter()

                }

            }

        }

        databinding.idToolBarLyt.back_button.setOnClickListener {
            finish()
        }


        databinding.conformBtn.setOnClickListener {

            AddVehicle.newInstance(
                getString(R.string.str_title),
                getString(R.string.str_sub_title),
                this@VehicleMgmtActivity
            ).show(supportFragmentManager, AddVehicle.TAG)

        }

    }

    private fun setHistoryAdapter() {
        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        val mAdapter = VrmHistoryAdapter(this)
        mAdapter.setList(vehicleResp.vehicleList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setListAdapter() {

        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        mAdapter = VrmHeaderAdapter(this)
        mAdapter.setList(vehicleResp.vehicleList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    override fun onAddClick(details: VehicleResponse) {

        val intent = Intent(this, VrmEditMakeModelColorActivity::class.java)
        intent.putExtra("list", details)
        startActivity(intent)

    }

    // todo add listener in inside the class


    private fun addVehicleApiCall() {

        var request = VehicleResponse(
            PlateInfoResponse(
                number = "HRS112022",
                "UK", "HE", type = "STANDARD", "", "New vehicle", ""
            ),
            VehicleInfoResponse("AUDI", "Q5", "2021", "", "", "", "BLACK", "Class B", "")
        )
        vehicleMgmtViewModel.addVehicleApi(request);
        vehicleMgmtViewModel.addVehicleApiVal.observe(this,
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data!!.body() == null) {
                            var apiResponse = EmptyApiResponse(200, "Added successfully.")
                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
                        }

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

    private fun updateVehicleApiCall() {

        var request = VehicleResponse(
            PlateInfoResponse(
                number = "HRS112022",
                "UK", "HE", type = "STANDARD", "", "New vehicle", ""
            ),
            VehicleInfoResponse("AUDI", "Q5", "2021", "", "REGULAR", "", "BLACK", "Class B", "")
        )
        vehicleMgmtViewModel.updateVehicleApi(request);
        vehicleMgmtViewModel.updateVehicleApiVal.observe(this,
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data!!.body() == null) {
                            var apiResponse = EmptyApiResponse(200, "Added successfully.")
                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
                        }

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
    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    }

}