package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VehicleInfoResponse(
    @SerializedName("make")
    val make: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("typeId")
    val typeId: String? = null,
    @SerializedName("rowId")
    val rowId: String,
    @SerializedName("typeDescription")
    val typeDescription: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("vehicleClassDesc")
    val vehicleClassDesc: String,
    @SerializedName("effectiveStartDate")
    val effectiveStartDate: String
) : Serializable

