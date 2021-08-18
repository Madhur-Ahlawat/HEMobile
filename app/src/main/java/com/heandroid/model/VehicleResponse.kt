package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @SerializedName("plateInfo")
    var plateInfo: PlateInfoResponse,

    @SerializedName("vehicleInfo")
    var vehicleInfo: VehicleInfoResponse,

    )

