package com.heandroid.listener

import com.heandroid.model.VehicleResponse

interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleResponse, pos: Int)

    fun onItemClick(details: VehicleResponse, pos: Int)

}