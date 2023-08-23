package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsResponse(
    var plateNumber: String?="",
    var customerClass: String?="",
    var customerClassRate: String?="",
    var chargingRate: String?="",
    var unSettledTrips: String?="",
    var unPaidAmt: String?="",
    var plateCountry: String?="",
    var vehicleType: Int=0,
    var vehicleMake: String?="",
    var vehicleModel: String?="",
    var vehicleYear: String?="",
    var vehicleColor: String?="",
    var accountNumber: String?="",
    var dvlaclass: String?="",
    var recieptMode:String?="",
    var countryCode:String?="",
    var recentCrossingCount:Int=0,
    var additionalCrossingCount:Int=0,
    var totalAmount:Double=0.0,
    var additionalCharge:Double=0.0,
    var veicleUKnonUK:Boolean=false,
    var vehicleInformationCheckbox:Boolean=false,
) : Parcelable

