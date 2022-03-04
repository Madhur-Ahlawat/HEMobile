package com.heandroid.ui.vehicle

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.heandroid.R
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.vehicle.vehiclehistory.VehicleHistoryCrossingHistoryFragment
import com.heandroid.ui.vehicle.vehiclehistory.VehicleHistoryVehicleDetailsFragment
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
        initCtrl()
        setView()
    }

    override fun observeViewModel() { }

    private fun initCtrl() {
        binding.idToolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setView() {
        mType = intent.extras?.getInt(Constants.VEHICLE_SCREEN_KEY)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        mType?.let {
            when (it) {
                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_list)
                    binding.chipLayout.gone()
                    navHost = navHostFragment.navController
                    navHost.setGraph(R.navigation.navigation_vehicle_list)
                }
                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_add_vehicles)
                    binding.chipLayout.gone()
                    navHost = navHostFragment.navController
                    navHost.setGraph(R.navigation.navigation_add_vehicle)
                }
                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    binding.idToolBarLyt.titleTxt.text = getString(R.string.str_vehicle_history)
                    navHost = navHostFragment.navController
                    navHost.setGraph(R.navigation.navigation_vehicle_history)

                    navHost.addOnDestinationChangedListener { _, destination, _ ->
                        when(destination.id){
                            R.id.vehicleHistoryVehicleDetailsFragment -> {
                                makeVehicleDetailsButton()
                                binding.chipLayout.visible()
                            }

                            R.id.vehicleHistoryCrossingHistoryFragment -> {
                                makeCrossingHistoryButton()
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
                    navHost = navHostFragment.navController
                    navHost.setGraph(R.navigation.navigation_crossing_history)
                }
            }
        }

        binding.vehicleDetailsTxt.setOnClickListener {
            makeVehicleDetailsButton()
            if (navHostFragment.childFragmentManager.fragments[0] !is VehicleHistoryVehicleDetailsFragment) {
                navHost.navigate(R.id.action_vehicleHistoryCrossingHistoryFragment_to_vehicleHistoryVehicleDetailsFragment)
            }
        }

        binding.crossingHistoryTxt.setOnClickListener {
            makeCrossingHistoryButton()
            if (navHostFragment.childFragmentManager.fragments[0] !is VehicleHistoryCrossingHistoryFragment) {
                navHost.navigate(R.id.action_vehicleHistoryVehicleDetailsFragment_to_vehicleHistoryCrossingHistoryFragment)
            }
        }

    }

    private fun makeVehicleDetailsButton(){
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
    }

    private fun makeCrossingHistoryButton() {
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
    }

}