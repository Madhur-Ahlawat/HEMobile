package com.heandroid.ui.vehicle

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleMgmtActivity : BaseActivity<ActivityVehicleMgmtBinding>() {

    private var mType: Int? = null
    private lateinit var binding: ActivityVehicleMgmtBinding
    private lateinit var navHost: NavController

    override fun initViewBinding() {
        binding = ActivityVehicleMgmtBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() { }

    override fun onResume() {
        super.onResume()
        setView()
    }

    private fun setView() {
        mType = Constants.VEHICLE_SCREEN_TYPE_HISTORY
        mType?.apply {
            when (this) {
                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_list)
                    binding.chipLayout.gone()
                    navHost = Navigation.findNavController(
                        this@VehicleMgmtActivity,
                        R.id.fragmentContainer
                    )
                    navHost.setGraph(R.navigation.navigation_vehicle_list)
                }
                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_add_vehicles)
                    binding.chipLayout.gone()
                    navHost = Navigation.findNavController(
                        this@VehicleMgmtActivity,
                        R.id.fragmentContainer
                    )
                    navHost.setGraph(R.navigation.navigation_add_vehicle)
                }
                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_history)
                    navHost = Navigation.findNavController(
                        this@VehicleMgmtActivity,
                        R.id.fragmentContainer
                    )
                    navHost.setGraph(R.navigation.navigation_vehicle_history)
                    navHost.addOnDestinationChangedListener { _, destination, _ ->
                        when(destination.id){
                            R.id.vehicleHistoryVehicleDetailsFragment -> {
                                binding.chipLayout.visible()
                            }

                            R.id.vehicleHistoryCrossingHistoryFragment -> {
                                binding.chipLayout.visible()
                            }
                            else -> {
                                binding.chipLayout.gone()
                            }
                        }
                    }
                }

                Constants.VEHICLE_SCREEN_TYPE_CROSSING_HISTORY -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.crossing_history)
                    binding.chipLayout.gone()
                    navHost = Navigation.findNavController(
                        this@VehicleMgmtActivity,
                        R.id.fragmentContainer
                    )
                    navHost.setGraph(R.navigation.navigation_crossing_history)
                }
            }
        }

        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.vehicleDetailsTxt.setOnClickListener {
            binding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            binding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            binding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            navHost.navigate(R.id.vehicleHistoryVehicleDetailsFragment)

        }

        binding.crossingHistoryTxt.setOnClickListener {
            binding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            binding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            binding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            navHost.navigate(R.id.vehicleHistoryCrossingHistoryFragment)
        }

    }

}