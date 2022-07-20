package com.heandroid.ui.vehicle.vehiclelist.dialog

import com.heandroid.data.model.vehicle.VehicleResponse


interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleResponse?, pos: Int)

    fun onItemClick(details: VehicleResponse?, pos: Int)

}