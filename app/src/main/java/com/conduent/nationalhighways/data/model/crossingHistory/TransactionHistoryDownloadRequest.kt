package com.conduent.nationalhighways.data.model.crossingHistory

import com.google.gson.annotations.SerializedName

data class TransactionHistoryDownloadRequest(

    @SerializedName("searchDate") var searchDate: String? = "",
    @SerializedName("startDate") var startDate: String? = "",
    @SerializedName("endDate") var endDate: String? = "",
    @SerializedName("startIndex") var startIndex: String? = "0",
    @SerializedName("transactionType") var transactionType: String? = "",
    @SerializedName("plateNumber") var plateNumber: String? = "",
    @SerializedName("downloadType") var downloadType: String? = ""

)
