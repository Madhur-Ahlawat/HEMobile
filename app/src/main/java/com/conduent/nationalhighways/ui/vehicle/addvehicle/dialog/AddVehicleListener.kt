package com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog

import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse

interface AddVehicleListener {
    fun onAddClick(details: VehicleResponse)
}