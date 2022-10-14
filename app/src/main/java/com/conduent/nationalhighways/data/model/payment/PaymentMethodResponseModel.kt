package com.conduent.nationalhighways.data.model.payment

data class PaymentMethodResponseModel(
    val creditCardListType: CreditCardListType?,
    val message: String?,
    val statusCode: String?
)