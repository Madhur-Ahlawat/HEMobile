package com.heandroid.data.model.payment

data class Wallet(
    val billingInfo: BillingInfo,
    val cardDetails: Any,
    val cardNetwork: Any,
    val email: Any,
    val shippingInfo: ShippingInfo
)