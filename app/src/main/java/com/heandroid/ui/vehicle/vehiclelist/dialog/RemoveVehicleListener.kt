package com.heandroid.ui.vehicle.vehiclelist.dialog

import com.heandroid.data.model.vehicle.VehicleResponse

interface RemoveVehicleListener {
    fun onRemoveClick(selectedVehicleList : List<String?>)
}