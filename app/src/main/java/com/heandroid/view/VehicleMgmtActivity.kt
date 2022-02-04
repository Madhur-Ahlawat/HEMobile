package com.heandroid.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleApiResp
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleMgmtActivity : AppCompatActivity(), AddVehicleListener, ItemClickListener {

    private val mList = ArrayList<VehicleResponse>()
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var vehicleListAdapter: VehicleListAdapter
    private lateinit var databinding: ActivityVehicleMgmtBinding
    private lateinit var mAdapter: VrmHeaderAdapter
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_mgmt)

        setupViewModel()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")

    }

    private var mType: Int? = null
    private fun setView() {

        mType = intent?.getIntExtra(Constants.VEHICLE_SCREEN_KEY, 0)

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    databinding.idToolBarLyt.title_txt.text = "Vehicles list"
                    databinding.progressLayout.visibility  = VISIBLE
                    getVehicleListApiCall()

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
                    getVehicleListApiCall()
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
        //val bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
       // val vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        val mAdapter = VrmHistoryAdapter(this, this)
        mAdapter.setList(mList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setListAdapter() {



        mAdapter = VrmHeaderAdapter(this, this)
        mAdapter.setList(mList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    override fun onAddClick(details: VehicleResponse) {

        val intent = Intent(this, VrmEditMakeModelColorActivity::class.java)
        intent.putExtra("list", details)
        intent.putExtra(Constants.VEHICLE_SCREEN_KEY, mType)
        startActivity(intent)

    }


    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {

                    details.isExpanded = !details.isExpanded
                    Logg.logging("VehcileMgm", "  details.isExpanded  ${details.isExpanded}")
                    mList[pos].isExpanded = details.isExpanded
                    mAdapter.notifyItemChanged(pos)

                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {

                    Intent(this@VehicleMgmtActivity, ActivityVehicleHistory::class.java).apply {
                        putExtra("list", details)
                        startActivity(this)

                    }

                }

            }

        }


    }

    override fun onResume() {
        super.onResume()
        databinding.progressLayout.visibility=VISIBLE
        setView()
    }

    private fun getVehicleListApiCall() {
        dashboardViewModel.getVehicleInformationApi()
        dashboardViewModel.vehicleListVal.observe(this, androidx.lifecycle.Observer {
            it.let { resource ->
                databinding.progressLayout.visibility  = GONE
                when (resource.status) {
                    Status.SUCCESS -> {
                        var vehicleList = resource.data!!.body()
                        if (vehicleList != null) {
                            mList.clear()
                            mList.addAll(vehicleList)
                            if(mType==Constants.VEHICLE_SCREEN_TYPE_LIST) {
                                setListAdapter()
                            }
                            else if(mType== Constants.VEHICLE_SCREEN_TYPE_HISTORY){
                                setHistoryAdapter()
                            }
                            else{
                                // do nothing
                            }
                            Log.d("apiResp:getVehicleListApiCall ", vehicleList.size.toString())
                            Log.d(
                                "apiResp:getVehicleListApiCall vehicleList.toString() ",
                                vehicleList.toString()
                            )
//                            setupVehicleData(vehicleList)
                        }

                    }
                    Status.ERROR -> {
                        Toast.makeText(this, resource.message, Toast.LENGTH_LONG)
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