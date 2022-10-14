package com.conduent.nationalhighways.data.model.payment

data class PaymentMethodEditModel   (
    val cardType: String?,
    val easyPay: String?,
    val paymentType: String?,
    val primaryCard: String?,
    val rowId: String?
)