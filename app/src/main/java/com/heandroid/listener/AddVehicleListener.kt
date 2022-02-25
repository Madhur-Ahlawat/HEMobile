package com.heandroid.listener

import com.heandroid.data.model.vehicle.VehicleResponse

interface AddVehicleListener {

     fun onAddClick(details: VehicleResponse)
}