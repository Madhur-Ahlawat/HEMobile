package com.heandroid.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CrossingDetailsModelsRequest(
    val plateNumber: String?,
    val customerClass: String?,
    val plateCountry: String?,
    val vehicleMake: String?,
    val vehicleModel: String?
) : Parcelable
