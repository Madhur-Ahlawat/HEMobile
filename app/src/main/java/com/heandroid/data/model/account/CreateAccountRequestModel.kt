package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountRequestModel(
    var accountType: String?,
    var address1: String?,
    val billingAddressLine1: String?,
    val billingAddressLine2: String?,
    val cardCity: String?,
    var cardFirstName: String?,
    var cardLastName: String?,
    var cardMiddleName: String?,
    val cardStateType: String?,
    val cardZipCode: String?,
    var cellPhone: String?,
    var city: String?,
    var countryType: String?,
    var creditCExpMonth: String?,
    var creditCExpYear: String?,
    var creditCardNumber: String?,
    var creditCardType: String?,
    val digitPin: String?,
    var emailAddress: String?,
    var eveningPhone: String?,
    var firstName: String?,
    val ftvehicleList: CreateAccountVehicleListModel?,
    var lastName: String?,
    var maskedNumber: String?,
    val password: String?,
    val replenishmentAmount: Double?=5.0,
    var securityCode: String?,
    val smsOption: String?,
    var stateType: String?,
    val tcAccepted: String?,
    val thresholdAmount: Double?=5.0,
    val transactionAmount: Double?=10.00,
    val zipCode1: String?,
    var enable : Boolean?,
) : Parcelable