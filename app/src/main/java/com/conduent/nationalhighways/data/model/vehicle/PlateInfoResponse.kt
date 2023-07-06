package com.conduent.nationalhighways.data.model.vehicle

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Parcelize
data class PlateInfoResponse(

    @SerializedName("number")
    var number: String? = "",
    @SerializedName("country")
    var country: String? = "UK",
    @SerializedName("state")
    var state: String? = "",
    @SerializedName("type")
    var type: String? = "STANDARD",
    @SerializedName("vehicleGroup")
    var vehicleGroup: String? = "",
    @SerializedName("vehicleComments")
    var vehicleComments: String? = "",
    @SerializedName("planName")
    var planName: String? = ""

) : Parcelable



