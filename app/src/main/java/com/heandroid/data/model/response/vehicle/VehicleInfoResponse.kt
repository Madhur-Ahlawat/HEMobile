package com.heandroid.data.model.response.vehicle

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VehicleInfoResponse(
    @SerializedName("make")
    var make: String="",
    @SerializedName("model")
    var model: String="",
    @SerializedName("year")
    var year: String ="",
    @SerializedName("typeId")
    var typeId: String? = null,
    @SerializedName("rowId")
    val rowId: String="",
    @SerializedName("typeDescription")
    var typeDescription: String="",
    @SerializedName("color")
    var color: String="",
    @SerializedName("vehicleClassDesc")
    var vehicleClassDesc: String="",
    @SerializedName("effectiveStartDate")
    val effectiveStartDate: String=""
) : Serializable

