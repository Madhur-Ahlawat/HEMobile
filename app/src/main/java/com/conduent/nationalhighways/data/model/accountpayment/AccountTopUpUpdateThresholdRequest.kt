package com.conduent.nationalhighways.data.model.accountpayment

data class AccountTopUpUpdateThresholdRequest(
    val thresholdAmount: String?,
    val customerAmount: String?
)