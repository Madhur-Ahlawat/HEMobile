package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlateInfoResponse(
    @SerializedName("number")
    var number: String,
    @SerializedName("country")
    var country: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("type")
    var type: String,
    val vehicleGroup: String,
    val vehicleComments: String, val planName: String
):Serializable


