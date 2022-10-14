package com.conduent.nationalhighways.data.model.accountpayment

data class AccountPaymentHistoryRequest(
    val startIndex: Int? = 1,
    val transactionType: String? = "Payment",
    val count: Int? = 2,
    var startDate: String? = null,
    var endDate: String? = null,
    val plateNumber: String? = "",
    var sortOrder: String? = "DESC"
)