package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OneOfPaymentModelRequest(
    val ftvehicleList: FtVehicleList?,
    val paymentInfo: PaymentTypeInfo?
) : Parcelable

@Parcelize
data class FtVehicleList(val vehicle: ArrayList<VehicleList>?) : Parcelable

@Parcelize
data class PaymentTypeInfo(
    val creditCardType: String?,
    val maskedCardNumber: String?,
    val creditCardNumber: String?,
    val creditCExpMonth: String?,
    val creditCExpYear: String?,
    val totalTransactionAmount: String?,
    val firstName: String?,
    val lastName: String?,
    val emailAddress: String?,
    val phoneNum: String?,
    val billingAddressLine: String?,
    val billingAddressLine2: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val zipcode: String?,
    val zipCode2: String?
):Parcelable

@Parcelize
data class VehicleList(
    val vehiclePlate: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val iagCode: String?,
    val plateCountry: String?,
    val pendingDues: String?,
    val futureTollCount: String?,
    val futureTollPayment: String?,
    val vehicleColour: String?,
    val classRate: String?,
    val customerClass: String?,
    val customerClassRate: String?,
    val accountNumber: String?,
    val pendingTxnCount: String?,
    val chargingRate: String?
):Parcelable


