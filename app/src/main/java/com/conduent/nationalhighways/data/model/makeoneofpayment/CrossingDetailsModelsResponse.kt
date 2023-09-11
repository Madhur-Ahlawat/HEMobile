package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrossingDetailsModelsResponse(
    var plateNumberToTransfer: String?="",
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
    var dvlaclass: String?="",
    var recieptMode:String?="",
    var countryCode:String?="",
    var additionalCrossingCount:Int=0,
    var totalAmount:Double=0.0,
    var additionalCharge:Double=0.0,
    var veicleUKnonUK:Boolean=false,
    var vehicleInformationCheckbox:Boolean=false,
    var accountNo: String="",
    var accountTypeCd: String="",
    var accountType: String="",
    var accountActStatus: String="",
    var accountBalance: String="",
    var plateNo: String="",
    var accountStatusCd: String="",
    var unusedTrip: String="",
    var expirationDate: String="",
    var referenceNumber: String = "",
    var tripCount: String?="0",
    var plateState: String?="HE",
    var isExempted: String?="",
    var isRUCEligible: String?="",
    var vehicleClass: String?="",
    var vehicleClassBalanceTransfer: String?=""
) : Parcelable

