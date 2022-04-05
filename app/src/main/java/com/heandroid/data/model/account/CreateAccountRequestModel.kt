package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountRequestModel(
    var referenceId: String?,
    var securityCd: String?,
    var accountType: String?,
    var address1: String?,
    var planType: String?,
    var billingAddressLine1: String?,
    var billingAddressLine2: String?,
    var cardCity: String?,
    var cardFirstName: String?,
    var cardLastName: String?,
    var cardMiddleName: String?,
    var cardStateType: String?,
    var cardZipCode: String?,
    var cellPhone: String?,
    var city: String?,
    var countryType: String?,
    var creditCExpMonth: String?,
    var creditCExpYear: String?,
    var creditCardNumber: String?,
    var creditCardType: String?,
    var digitPin: String?,
    var emailAddress: String?,
    var eveningPhone: String?,
    var firstName: String?,
    var ftvehicleList: CreateAccountVehicleListModel?,
    var lastName: String?,
    var maskedNumber: String?,
    var password: String?,
    var replenishmentAmount: Double?=5.0,
    var securityCode: String?,
    var smsOption: String?,
    var stateType: String?,
    val tcAccepted: String?,
    var thresholdAmount: Double?=5.0,
    var transactionAmount: Double?=10.00,
    var zipCode1: String?,
    var enable : Boolean?,

    ) : Parcelable