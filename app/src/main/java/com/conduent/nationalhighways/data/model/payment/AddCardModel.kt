package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddCardModel(
    var addressLine1: String?,
    var addressLine2: String?,
    val bankRoutingNumber: String?,
    val cardNumber: String?,
    var cardType: String?,
    var city: String?,
    var country: String?,
    val customerVaultId: String?,
    var easyPay: String?,
    val expMonth: String?,
    var expYear: String?,
    var firstName: String?,
    var lastName: String?,
    val maskedCardNumber: String?,
    var middleName: String?,
    val paymentType: String?,
    var primaryCard: String?,
    var saveCard: String?="N",
    var useAddressCheck: String?="N",
    var state: String?,
    var zipcode1: String?,
    var zipcode2: String?,
    val cvv: String?,
    var default: Boolean?=false
) : Parcelable