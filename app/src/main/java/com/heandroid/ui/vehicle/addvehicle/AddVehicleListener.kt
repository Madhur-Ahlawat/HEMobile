package com.heandroid.ui.vehicle.addvehicle

import com.heandroid.data.model.response.vehicle.VehicleResponse

interface AddVehicleListener {
    fun onAddClick(details: VehicleResponse)
}