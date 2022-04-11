package com.heandroid.data.model.manualtopup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PaymentWithNewCardModel(
    val addressline1: String?="",
    val addressline2: String?="",
    val bankRoutingNumber: String?,
    val cardNumber: String?,
    val cardType: String?,
    val city: String?="",
    val country: String?="",
    val cvv: String?,
    val easyPay: String?,
    val expMonth: String?,
    val expYear: String?,
    var firstName: String?,
    var lastName: String?,
    val maskedNumber: String?,
    var middleName: String?,
    val paymentType: String?,
    val primaryCard: String?,
    val saveCard: String?,
    val state: String="",
    val transactionAmount: String?,
    val useAddressCheck: String="N",
    val zipcode1: String="",
    val zipcode2: String=""
) : Parcelable