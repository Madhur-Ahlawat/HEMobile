package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsResponse(
    val plateNumber: String?,
    val customerClass: String?,
    val customerClassRate: String?,
    val chargingRate: String?,
    var unSettledTrips: String?,
    val unPaidAmt: String?,
    val plateCountry: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehicleYear: String?,
    val accountNumber: String?,
    val dvlaclass: String?
) : Parcelable

