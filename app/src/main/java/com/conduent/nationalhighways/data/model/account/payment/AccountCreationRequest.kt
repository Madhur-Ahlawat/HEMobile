package com.conduent.nationalhighways.data.model.account.payment

import com.google.gson.annotations.SerializedName

data class AccountCreationRequest(

    @field:SerializedName("eveningPhone")
    var eveningPhone: String? = null,

    @field:SerializedName("lastName")
    var lastName: String? = null,

    @field:SerializedName("zipCode1")
    var zipCode1: String? = null,

    @field:SerializedName("billingAddressLine1")
    var billingAddressLine1: String? = null,

    @field:SerializedName("city")
    var city: String? = null,

    @field:SerializedName("stateType")
    var stateType: String? = null,

    @field:SerializedName("smsReferenceId")
    var smsReferenceId: String? = null,

    @field:SerializedName("mailPreference")
    var mailPreference: String? = null,

    @field:SerializedName("eci")
    var eci: String? = null,

    @field:SerializedName("cardFirstName")
    var cardFirstName: String? = null,

    @field:SerializedName("creditCardType")
    var creditCardType: String? = null,

    @field:SerializedName("digitPin")
    var digitPin: String? = null,

    @field:SerializedName("countryType")
    var countryType: String? = null,

    @field:SerializedName("referenceId")
    var referenceId: String? = null,

    @field:SerializedName("cardholderAuth")
    var cardholderAuth: String? = null,

    @field:SerializedName("securityCd")
    var securityCd: String? = null,

    @field:SerializedName("tcAccepted")
    var tcAccepted: String? = null,

    @field:SerializedName("emailAddress")
    var emailAddress: String? = null,

    @field:SerializedName("maskedNumber")
    var maskedNumber: String? = null,

    @field:SerializedName("password")
    var password: String? = null,

    @field:SerializedName("smsOption")
    var smsOption: String? = null,

    @field:SerializedName("cardLastName")
    var cardLastName: String? = null,

    @field:SerializedName("transactionAmount")
    var transactionAmount: String? = null,

    @field:SerializedName("cardMiddleName")
    var cardMiddleName: String? = null,

    @field:SerializedName("cardStateType")
    var cardStateType: String? = null,

    @field:SerializedName("eveningPhoneCountryCode")
    var eveningPhoneCountryCode: String? = null,

    @field:SerializedName("ftvehicleList")
    var ftvehicleList: FtvehicleList? = null,

    @field:SerializedName("emailPreference")
    var emailPreference: String? = null,

    @field:SerializedName("correspDeliveryMode")
    var correspDeliveryMode: String? = null,

    @field:SerializedName("cardCity")
    var cardCity: String? = null,

    @field:SerializedName("cardZipCode")
    var cardZipCode: String? = null,

    @field:SerializedName("thresholdAmount")
    var thresholdAmount: String? = null,

    @field:SerializedName("smsSecurityCd")
    var smsSecurityCd: String? = null,

    @field:SerializedName("address1")
    var address1: String? = null,

    @field:SerializedName("accountType")
    var accountType: String? = null,

    @field:SerializedName("securityCode")
    var securityCode: String? = null,

    @field:SerializedName("threeDsVer")
    var threeDsVer: String? = null,

    @field:SerializedName("correspDeliveryFrequency")
    var correspDeliveryFrequency: String? = null,

    @field:SerializedName("replenishmentAmount")
    var replenishmentAmount: String? = null,

    @field:SerializedName("cavv")
    var cavv: String? = null,

    @field:SerializedName("firstName")
    var firstName: String? = null,

    @field:SerializedName("creditCExpYear")
    var creditCExpYear: String? = null,

    @field:SerializedName("creditCExpMonth")
    var creditCExpMonth: String? = null,

    @field:SerializedName("creditCardNumber")
    var creditCardNumber: String? = null,

    @field:SerializedName("directoryServerID")
    var directoryServerID: String? = null,

    @field:SerializedName("mfaFlag")
    var mfaFlag: String? = null
)

data class VehicleItem(

    @field:SerializedName("plateCountry")
    var plateCountry: String? = null,

    @field:SerializedName("vehicleColor")
    var vehicleColor: String? = null,

    @field:SerializedName("vehicleYear")
    var vehicleYear: String? = null,

    @field:SerializedName("vehiclePlate")
    var vehiclePlate: String? = null,

    @field:SerializedName("plateTypeDesc")
    var plateTypeDesc: String? = null,

    @field:SerializedName("vehicleModel")
    var vehicleModel: String? = null,

    @field:SerializedName("vehicleClassDesc")
    var vehicleClassDesc: String? = null,

    @field:SerializedName("vehicleMake")
    var vehicleMake: String? = null
)

data class FtvehicleList(

    @field:SerializedName("vehicle")
    var vehicle: MutableList<VehicleItem?>? = ArrayList()
)
