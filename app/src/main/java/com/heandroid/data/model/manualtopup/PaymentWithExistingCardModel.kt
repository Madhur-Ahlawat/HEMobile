package com.heandroid.data.model.manualtopup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PaymentWithExistingCardModel(
    val addressline1: String?="",
    val addressline2: String?="",
    val cardNumber: String?,
    val cardType: String?,
    val city: String?="",
    val country: String?="",
    val cvv: String?,
    val easyPay: String?,
    val firstName: String?,
    val lastName: String?,
    val maskedCardNumber: String?,
    val middleName: String?,
    val paymentType: String?,
    val primaryCard: String?,
    val rowId: String?,
    val saveCard: String?,
    val state: String?="",
    val useAddressCheck: String?="N",
    val zipcode1: String?="",
    val zipcode2: String?="",
    val transactionAmount: String?
) : Parcelable




