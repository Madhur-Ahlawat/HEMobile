package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VehicleResponse(
    @SerializedName("plateInfo")
    var plateInfo: PlateInfoResponse,

    @SerializedName("vehicleInfo")
    var vehicleInfo: VehicleInfoResponse,

    ):Serializable

