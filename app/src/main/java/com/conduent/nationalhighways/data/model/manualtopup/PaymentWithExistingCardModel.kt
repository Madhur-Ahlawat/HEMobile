package com.conduent.nationalhighways.data.model.manualtopup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PaymentWithExistingCardModel(
    var addressline1: String? = "",
    var addressline2: String? = "",
    val cardNumber: String?,
    val cardType: String?,
    var city: String? = "",
    var country: String? = "",
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
    var state: String? = "",
    val useAddressCheck: String? = "N",
    var zipcode1: String? = "",
    var zipcode2: String? = "",
    val transactionAmount: String?,
    var cavv: String? = "",
    var xid: String? = "",
    var threeDsVersion: String? = "",
    var directoryServerId: String? = "",
    var cardHolderAuth: String? = "",
    var eci: String? = ""

) : Parcelable




