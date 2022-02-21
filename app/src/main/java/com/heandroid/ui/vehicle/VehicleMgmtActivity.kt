package com.heandroid.ui.vehicle

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.gone
import com.heandroid.ui.vehicle.vehiclehistory.VrmHistoryAdapter
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.utils.Resource
import com.heandroid.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleMgmtActivity : AppCompatActivity(), AddVehicleListener, ItemClickListener {

    private val mList = ArrayList<VehicleResponse>()
    private var mType: Int? = null
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private lateinit var vehicleListAdapter: VehicleListAdapter
    private lateinit var databinding: ActivityVehicleMgmtBinding

    private lateinit var navHost : NavController
    private var vehicleHistoryItem : Bundle? = null
//    private lateinit var mAdapter: VrmHeaderAdapter
//    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_mgmt)

//        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

//    private fun setupViewModel() {
//        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
//        Log.d("ViewModelSetUp: ", "Setup")
//        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
//        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
//        Log.d("ViewModelSetUp: ", "Setup")
//
//    }


    private fun setView() {

        mType = Constants.VEHICLE_SCREEN_TYPE_HISTORY

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    databinding.idToolBarLyt.titleTxt.text = "Vehicles list"
                    databinding.progressLayout.visibility = VISIBLE
                    getVehicleListApiCall()
                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    databinding.idToolBarLyt.titleTxt.text = "Add vehicles"
                    databinding.addVehiclesTxt.text = "Add vehicles to your account"
                    databinding.conformBtn.text = "Add Vehicle"
                    databinding.progressLayout.visibility = GONE
                    databinding.conformBtn.icon = AppCompatResources.getDrawable(
                        this@VehicleMgmtActivity,
                        R.drawable.ic_baseline_add_24
                    )
                    databinding.conformBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    databinding.conformBtn.iconTint = ColorStateList.valueOf(Color.WHITE)
                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    databinding.idToolBarLyt.titleTxt.text = "Vehicles History"
                    databinding.conformBtn.visibility = View.GONE
                    databinding.progressLayout.visibility = VISIBLE

                    getVehicleListApiCall()
                }

            }

        }

        databinding.idToolBarLyt.backButton.setOnClickListener {
            finish()
        }


        databinding.conformBtn.setOnClickListener {
//
//            AddVehicle.newInstance(
//                getString(R.string.str_title),
//                getString(R.string.str_sub_title),
//                this@VehicleMgmtActivity
//            ).show(supportFragmentManager, AddVehicle.TAG)

        }

        databinding.vehicleDetailsTxt.setOnClickListener {

            databinding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            databinding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            databinding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            databinding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            vehicleHistoryItem?.let {
                navHost.navigate(R.id.vehicleHistoryVehicleDetailsFragment, it)
            }
        }

        databinding.crossingHistoryTxt.setOnClickListener {

            databinding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            databinding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            databinding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            databinding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            vehicleHistoryItem?.let {
                navHost.navigate(R.id.vehicleHistoryCrossingHistoryFragment, it)
            }
        }

    }


    private fun setHistoryAdapter() {
        val mAdapter = VrmHistoryAdapter(this)
        mAdapter.setList(mList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setListAdapter() {


//        mAdapter = VrmHeaderAdapter(this, this)
//        mAdapter.setList(mList)
//        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
//        databinding.recyclerViewHeader.setHasFixedSize(true)
//        databinding.recyclerViewHeader.adapter = mAdapter

    }

    override fun onAddClick(details: VehicleResponse) {

//        val intent = Intent(this, VrmEditMakeModelColorActivity::class.java)
//        intent.putExtra("list", details)
//        intent.putExtra(Constants.VEHICLE_SCREEN_KEY, mType)
//        startActivity(intent)

    }


    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {

        mType?.apply {
            when (this) {
                Constants.VEHICLE_SCREEN_TYPE_LIST -> {

                    details.isExpanded = !details.isExpanded
                    mList[pos].isExpanded = details.isExpanded
//                    mAdapter.notifyItemChanged(pos)
                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    databinding.chipLayout.visible()
                    databinding.fragmentContainer.visible()
                    databinding.addVehiclesTxt.gone()
                    databinding.recyclerViewHeader.gone()
                    vehicleHistoryItem = Bundle().apply {
                        putSerializable(Constants.DATA, details)
                    }
                    navHost = Navigation.findNavController(this@VehicleMgmtActivity, R.id.fragmentContainer)
                    navHost.setGraph(R.navigation.vehicle_history_nav_graph, vehicleHistoryItem)
                    navHost.navigate(R.id.vehicleHistoryVehicleDetailsFragment, vehicleHistoryItem)
                }
            }
        }
    }


    private fun getVehicleListApiCall() {
        vehicleMgmtViewModel.getVehicleInformationApi()
        vehicleMgmtViewModel.vehicleListVal.observe(this, { resource ->
            when (resource) {
                is Resource.Success -> {
                    databinding.progressLayout.visibility = GONE
                    resource.data!!.body()?.let {
                        mList.clear()
                        mList.addAll(it)
                        if (mType == Constants.VEHICLE_SCREEN_TYPE_LIST) {
                            setListAdapter()
                        } else if (mType == Constants.VEHICLE_SCREEN_TYPE_HISTORY) {
                            setHistoryAdapter()
                        } else {
                            // do nothing
                        }
                    }
                }
                is Resource.DataError -> {
                    databinding.progressLayout.visibility = GONE
                    Toast.makeText(this, resource.errorMsg, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    databinding.progressLayout.visibility = VISIBLE
                }
            }
        })
    }
}