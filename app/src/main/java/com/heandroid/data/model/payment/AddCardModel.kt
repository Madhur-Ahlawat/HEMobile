package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddCardModel(
    val addressLine1: String??,
    val addressLine2: String?,
    val bankRoutingNumber: String?,
    val cardNumber: String?,
    val cardType: String?,
    val city: String?,
    val country: String?,
    val customerVaultId: String?,
    val easyPay: String?,
    val expMonth: String?,
    val expYear: String?,
    var firstName: String?,
    var lastName: String?,
    val maskedCardNumber: String?,
    var middleName: String?,
    val paymentType: String?,
    val primaryCard: String?,
    val state: String?,
    val zipcode1: String?,
    val zipcode2: String?,
    val cvv: String?,
    var default: Boolean=false
) : Parcelable