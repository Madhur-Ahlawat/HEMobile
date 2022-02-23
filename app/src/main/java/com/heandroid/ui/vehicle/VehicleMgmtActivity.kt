package com.heandroid.ui.vehicle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.gone
import com.heandroid.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleMgmtActivity : AppCompatActivity() {

    private var mType: Int? = null
    private lateinit var databinding: ActivityVehicleMgmtBinding
    private lateinit var navHost : NavController
    private var vehicleHistoryItem : Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_mgmt)
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

    private fun setView() {
        mType = Constants.VEHICLE_SCREEN_TYPE_LIST
        mType?.apply {
            when (this) {
                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    databinding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_list)
                    databinding.chipLayout.gone()
                    navHost = Navigation.findNavController(this@VehicleMgmtActivity, R.id.fragmentContainer)
                    navHost.setGraph(R.navigation.vehicle_list_nav_graph)
                }
                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    databinding.idToolBarLyt.titleTxt.text = getString(R.string.str_add_vehicles)
                    databinding.chipLayout.gone()
                    navHost = Navigation.findNavController(this@VehicleMgmtActivity, R.id.fragmentContainer)
                    navHost.setGraph(R.navigation.add_vehicle_nav_graph)
                }
                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    databinding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_history)
                    navHost = Navigation.findNavController(this@VehicleMgmtActivity, R.id.fragmentContainer)
                    navHost.setGraph(R.navigation.vehicle_history_nav_graph)
                    navHost.addOnDestinationChangedListener { _, destination, _ ->
                        when (navHost.currentDestination) {
                            destination -> {

                            }
                        }
                    }
                }

                Constants.VEHICLE_SCREEN_TYPE_CROSSING_HISTORY -> {
                    databinding.idToolBarLyt.titleTxt.text = getString(R.string.crossing_history)
                    databinding.chipLayout.gone()
                    navHost = Navigation.findNavController(this@VehicleMgmtActivity, R.id.fragmentContainer)
                    navHost.setGraph(R.navigation.crossing_history_nav_graph)
                }
            }
        }

        databinding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
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

    fun setVehicleItem(item: VehicleResponse){
        vehicleHistoryItem = Bundle().apply {
            putSerializable(Constants.DATA, item)
        }
    }
}