package com.heandroid.data.model.vehicle

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable




@Parcelize
data class VehicleResponse(

    @SerializedName("newPlateInfo")
    var newPlateInfo: PlateInfoResponse,

    @SerializedName("plateInfo")
    var plateInfo: PlateInfoResponse,

    @SerializedName("vehicleInfo")
    var vehicleInfo: VehicleInfoResponse,

    var isExpanded: Boolean = false,

    var quantity : Int =0,
    var price : Double =0.0,

) : Parcelable

