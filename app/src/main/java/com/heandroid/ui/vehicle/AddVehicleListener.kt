package com.heandroid.ui.vehicle

import com.heandroid.data.model.response.vehicle.VehicleResponse

interface AddVehicleListener {
    public fun onAddClick(details: VehicleResponse)
}