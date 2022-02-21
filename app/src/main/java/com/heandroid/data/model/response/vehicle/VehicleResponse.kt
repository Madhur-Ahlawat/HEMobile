package com.heandroid.data.model.response.vehicle

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VehicleResponse(

    @SerializedName("newPlateInfo")
    var newPlateInfo: PlateInfoResponse,

    @SerializedName("plateInfo")
    var plateInfo: PlateInfoResponse,

    @SerializedName("vehicleInfo")
    var vehicleInfo: VehicleInfoResponse,

    var isExpanded: Boolean = false

) : Serializable

