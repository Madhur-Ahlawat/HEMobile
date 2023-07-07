package com.conduent.nationalhighways.data.model.manualtopup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PaymentWithNewCardModel(
    var addressLine1: String?="",
    var addressLine2: String?="",
    var bankRoutingNumber: String?,
    var cardNumber: String?,
    var cardType: String?,
    var city: String?="",
    var country: String?="",
    var cvv: String?,
    var easyPay: String?,
    var expMonth: String?,
    var expYear: String?,
    var firstName: String?,
    var middleName: String?,
    var lastName: String?,
    var maskedNumber: String?,
    var paymentType: String?,
    var primaryCard: String?,
    var saveCard: String?,
    var state: String?="",
    var transactionAmount: String?,
    var useAddressCheck: String?="N",
    var zipcode1: String?="",
    var zipcode2: String?="",
    var directoryServerId:String="",
    var cavv:String="",
    var xid:String="",
    var threeDsVersion:String="",
    var cardHolderAuth:String="",
    var eci:String=""
) : Parcelable