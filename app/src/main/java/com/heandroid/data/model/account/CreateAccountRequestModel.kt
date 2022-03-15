package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountRequestModel(
    val accountType: String?,
    val address1: String?,
    val billingAddressLine1: String?,
    val billingAddressLine2: String?,
    val cardCity: String?,
    val cardFirstName: String?,
    val cardLastName: String?,
    val cardMiddleName: String?,
    val cardStateType: String?,
    val cardZipCode: String?,
    val cellPhone: String?,
    val city: String?,
    val countryType: String?,
    val creditCExpMonth: String?,
    val creditCExpYear: String?,
    val creditCardNumber: String?,
    val creditCardType: String?,
    val digitPin: String?,
    val emailAddress: String?,
    val eveningPhone: String?,
    val firstName: String?,
    val ftvehicleList: CreateAccountVehicleListModel?,
    val lastName: String?,
    val maskedNumber: String?,
    val password: String?,
    val replenishmentAmount: String?,
    val securityCode: String?,
    val smsOption: String?,
    val stateType: String?,
    val tcAccepted: String?,
    val thresholdAmount: String?,
    val transactionAmount: String?,
    val zipCode1: String?
) : Parcelable