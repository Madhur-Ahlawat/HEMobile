package com.heandroid.utils.common

import com.heandroid.data.model.vehicle.VehicleResponse

object VehicleHelper {

     val list : MutableList<VehicleResponse?>? by  lazy  {  ArrayList() }
     val createAccountList : MutableList<VehicleResponse?>? by lazy { ArrayList() }

    fun addVehicle(model : VehicleResponse?){
        list?.add(model)
    }

    fun removeVehicle(position: Int?){
        list?.removeAt(position?:0)
    }

    fun addVehicleCreateAcc(model : VehicleResponse?){
        createAccountList?.add(model)
    }

    fun removeVehicleCreateAcc(position: Int?){
        createAccountList?.removeAt(position?:0)
    }

}