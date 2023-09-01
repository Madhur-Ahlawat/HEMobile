package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsResponse(
    var plateNumber: String?="",
    var customerClass: String?="",
    var customerClassRate: String?="",
    var chargingRate: String?="",
    var unSettledTrips: Int=0,
    var unPaidAmt: String?="",
    var plateCountry: String?="",
    var vehicleType: String?="",
    var vehicleMake: String?="",
    var vehicleModel: String?="",
    var vehicleYear: String?="",
    var vehicleColor: String?="",
    var accountNumber: String?="",
    var dvlaclass: String?="",
    var recieptMode:String?="",
    var countryCode:String?="",
    var additionalCrossingCount:Int=0,
    var totalAmount:Double=0.0,
    var additionalCharge:Double=0.0,
    var veicleUKnonUK:Boolean=false,
    var vehicleInformationCheckbox:Boolean=false,
    val accountNo: String="",
    val accountTypeCd: String="",
    val accountType: String="",
    val accountActStatus: String="",
    val accountStatusCd: String="",
    val unusedTrip: String="",
    val expirationDate: String="",
    var referenceNumber: String = "",
    val tripCount: String?="0",
    val plateState: String?="HE",
    val vehicleClass: String?=""
) : Parcelable

