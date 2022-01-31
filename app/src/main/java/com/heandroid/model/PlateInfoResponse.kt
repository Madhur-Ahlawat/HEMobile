package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlateInfoResponse(
    @SerializedName("type")
    var type: String="",
    @SerializedName("vehicleGroup")
    var vehicleGroup: String="",
    @SerializedName("vehicleComments")
    var vehicleComments: String="",
    @SerializedName("planName")
    var planName: String="",
):Serializable


