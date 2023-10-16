package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsRequest(
    val plateNumber: String?,
    val customerClass: String?,
    val plateCountry: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehicleColor:String="",
    val vehicleClass:String="",
    val dataType:String="",

) : Parcelable
