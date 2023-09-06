package com.conduent.nationalhighways.data.model.accountpayment
data class CheckedCrossingTransactionsRequestModel(
    var startIndex:Int?=0,
    var count:Int?=5,
    var transactionType:String?="TOLL",
    var sortColumn:String?="POSTED_DATE")
