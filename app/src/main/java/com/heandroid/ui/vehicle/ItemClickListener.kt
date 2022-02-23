package com.heandroid.ui.vehicle

import com.heandroid.data.model.response.vehicle.VehicleResponse

interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleResponse, pos: Int)

    fun onItemClick(details: VehicleResponse, pos: Int)

}