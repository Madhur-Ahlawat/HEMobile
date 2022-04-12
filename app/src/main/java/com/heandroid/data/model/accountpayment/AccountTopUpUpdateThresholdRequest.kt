package com.heandroid.data.model.accountpayment

data class AccountTopUpUpdateThresholdRequest(
    val thresholdAmount: String?,
    val customerAmount: String?
)