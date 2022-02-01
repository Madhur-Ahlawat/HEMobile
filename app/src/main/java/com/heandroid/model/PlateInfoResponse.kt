package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlateInfoResponse(

    @SerializedName("number")
    var number : String ="",
    @SerializedName("country")
    var country :String = "",
    @SerializedName("state")
    var state:String="",
    @SerializedName("type")
    var type: String="STANDARD",
    @SerializedName("vehicleGroup")
    var vehicleGroup: String="",
    @SerializedName("vehicleComments")
    var vehicleComments: String="",
    @SerializedName("planName")
    var planName: String="",


):Serializable



