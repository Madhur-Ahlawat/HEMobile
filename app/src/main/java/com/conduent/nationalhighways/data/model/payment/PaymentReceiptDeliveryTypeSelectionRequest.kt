package com.conduent.nationalhighways.data.model.payment

data class PaymentReceiptDeliveryTypeSelectionRequest(
    var paymentRefNumber: String?,
    var deliveryType: String?
)
