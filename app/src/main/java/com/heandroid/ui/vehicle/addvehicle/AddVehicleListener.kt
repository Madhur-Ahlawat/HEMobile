package com.heandroid.ui.vehicle.addvehicle

import com.heandroid.data.model.vehicle.VehicleResponse

interface AddVehicleListener {
    fun onAddClick(details: VehicleResponse)
}