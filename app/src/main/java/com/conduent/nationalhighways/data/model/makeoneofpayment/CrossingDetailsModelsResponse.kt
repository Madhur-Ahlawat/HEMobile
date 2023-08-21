package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsResponse(
    val plateNumber: String?,
    val customerClass: String?,
    var customerClassRate: String?,
    var chargingRate: String?,
    var unSettledTrips: String?,
    val unPaidAmt: String?,
    val plateCountry: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehicleYear: String?,
    val accountNumber: String?,
    val dvlaclass: String?,
    var recieptMode:String?,
    var crossingCount:Int,
    var additionalCrossingCount:Int,
    var totalAmount:Double,
    var additionalCharge:Double
) : Parcelable

