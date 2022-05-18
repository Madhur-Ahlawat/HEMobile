package com.heandroid.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CrossingDetailsModelsResponse(
    val plateNumber: String,
    val customerClass: String,
    val customerClassRate: String,
    val chargingRate: String,
    val unSettledTrips: String,
    val unPaidAmt: String,
    val plateCountry: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val vehicleYear: String,
    val accountNumber: String,
    val dvlaclass: String
) : Parcelable

