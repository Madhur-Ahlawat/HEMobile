package com.heandroid.ui.vehicle.addvehicle.dialog

import com.heandroid.data.model.vehicle.VehicleResponse

interface AddVehicleListener {
    fun onAddClick(details: VehicleResponse)
}