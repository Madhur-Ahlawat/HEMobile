package com.heandroid.listener

import com.heandroid.model.VehicleDetailsModel

interface ItemClickListener {

    fun onItemDeleteClick(details: VehicleDetailsModel, pos: Int)

    fun onItemClick(details: VehicleDetailsModel, pos: Int)

}