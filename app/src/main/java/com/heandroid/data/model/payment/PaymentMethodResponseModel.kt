package com.heandroid.data.model.payment

data class PaymentMethodResponseModel(
    val creditCardListType: CreditCardListType?,
    val message: String?,
    val statusCode: String?
)