package com.heandroid.listener

import com.heandroid.data.model.vehicle.VehicleResponse

interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleResponse, pos: Int)

    fun onItemClick(details: VehicleResponse, pos: Int)

}