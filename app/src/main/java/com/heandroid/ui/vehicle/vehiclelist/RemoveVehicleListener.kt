package com.heandroid.ui.vehicle.vehiclelist

import com.heandroid.data.model.vehicle.VehicleResponse

interface RemoveVehicleListener {
    fun onRemoveClick(selectedVehicleList : List<String?>)
}