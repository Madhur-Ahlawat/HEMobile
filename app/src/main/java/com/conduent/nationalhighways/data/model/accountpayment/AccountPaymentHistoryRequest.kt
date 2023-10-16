package com.conduent.nationalhighways.data.model.accountpayment

data class AccountPaymentHistoryRequest(
    val startIndex: Int? = 1,
    val transactionType: String? = "ALL",
    val count: Int? = 20,
    var sortOrder: String? = "DESC"
)