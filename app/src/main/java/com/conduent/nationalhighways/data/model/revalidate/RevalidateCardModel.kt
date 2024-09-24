package com.conduent.nationalhighways.data.model.revalidate

data class RevalidateCardModel(

    val cardType: String = "",
    val cvv: String = "",
    val rowId: String = "",
    val cardVerify: String = "",
    val source: String = "",
    val saveCard: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val addressline1: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val zipcode1: String = "",
    val customerVaultId: String = "",
    val paymentType: String = "",
    val primaryCard: String = "",
    val easyPay: String = "",
    val cardCVV: String = "",
    val cavv: String = "",
    val xid: String = "",
    val directoryServerId: String = "",
    val eci: String = "",
    val cardholderAuth: String = "",
    val threeDsVer: String = "",
)