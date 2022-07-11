package com.heandroid.data.model.vehicle

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleResponse(

    @SerializedName("newPlateInfo")
    var newPlateInfo: PlateInfoResponse?,

    @SerializedName("plateInfo")
    var plateInfo: PlateInfoResponse?,

    @SerializedName("vehicleInfo")
    var vehicleInfo: VehicleInfoResponse?,

    var isExpanded: Boolean? = false,

    var futureQuantity: Int? = 0,
    var pastQuantity: Int? = 0,
    var price: Double? = 0.0,
    var classRate: Double? = 0.0,
    var pendingDues: Double? = 0.0

) : Parcelable

