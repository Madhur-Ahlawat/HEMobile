package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountRequestModel(
    var referenceId: Long?,
    var securityCd: Long?,
    var accountType: String? = null,
    val tcAccepted: String?,
    var planType: String?,
    var firstName: String?,  // payg with zipcode and without not required
    var lastName: String?, // payg with zipcode and without not required
    var address1: String?, //payg with zipcode and without not required
    var city: String?, //payg with zipcode and without not required
    var stateType: String?, //payg with and without zipcode not required
    var countryType: String?,//payg with zipcode and without zipcode not required
    var zipCode1: String?,//payg  without zipcode not required
    var emailAddress: String?,
    var cellPhone: String?,
    var cellPhoneCountryCode: String?,
    var eveningPhone: String?,
    var eveningPhoneCountryCode: String?,
    var smsOption: String?,  //payg with zipcode and without zipcode not required
    var password: String?,
    var digitPin: String?,
    var correspDeliveryMode: String?,
    var correspDeliveryFrequency: String?,
    var companyName: String?, //payg with zipcode and without zipcode & prepay not required
    var fein: String?,////payg with zipcode and without zipcode & prepay not required
    var nonRevenueOption: String?,// payg with zipcode and without zipcode not required
    var ftvehicleList: CreateAccountVehicleListModel?,
    var creditCardType: String?,
    var creditCardNumber: String?,
    var maskedNumber: String?,
    var creditCExpMonth: String?,
    var creditCExpYear: String?,
    var securityCode: String?,
    var cardFirstName: String?,
    var cardMiddleName: String?,
    var cardLastName: String?,
    var billingAddressLine1: String?,
    var billingAddressLine2: String?,
    var cardCity: String?,
    var cardStateType: String?,
    var cardZipCode: String?,
    var thresholdAmount: String?, // payg with zipcode and without zipcode not required
    var replenishmentAmount: String?,
    var transactionAmount: String?,
    var enable: Boolean?,
    var vehicleNo: String?,
    var mNoOfVehicles: String?,
    var mNoOfCrossings: String?,
    var plateCountryType: String?
) : Parcelable