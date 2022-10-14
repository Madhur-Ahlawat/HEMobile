package com.conduent.nationalhighways.utils.common

import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse

object VehicleHelper {

     val list : MutableList<VehicleResponse?>? by  lazy  {  ArrayList() }

    fun addVehicle(model : VehicleResponse?){
        list?.add(model)
    }

    fun removeVehicle(position: Int?){
        list?.removeAt(position?:0)
    }

}