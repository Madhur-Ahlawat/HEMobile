package com.heandroid.data.model.accountpayment

data class AccountPaymentHistoryRequest(
    val startIndex : Int? = 1,
    val transactionType : String? = "Payment",
    val count : Int? = 2
)