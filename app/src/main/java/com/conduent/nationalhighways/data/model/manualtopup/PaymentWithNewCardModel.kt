package com.conduent.nationalhighways.data.model.manualtopup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PaymentWithNewCardModel(
    var addressLine1: String?="",
    var addressLine2: String?="",
    val bankRoutingNumber: String?,
    val cardNumber: String?,
    val cardType: String?,
    var city: String?="",
    var country: String?="",
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
    var state: String?="",
    val transactionAmount: String?,
    val useAddressCheck: String?="N",
    var zipcode1: String?="",
    var zipcode2: String?=""
) : Parcelable