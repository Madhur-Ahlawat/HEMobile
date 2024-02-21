package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse


interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleResponse?, pos: Int)

    fun onItemClick(details: VehicleResponse?, pos: Int)

}