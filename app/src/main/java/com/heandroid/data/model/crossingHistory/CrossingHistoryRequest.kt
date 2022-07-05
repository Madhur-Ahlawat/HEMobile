package com.heandroid.data.model.crossingHistory

import com.google.gson.annotations.SerializedName

data class CrossingHistoryRequest(
    @SerializedName("searchDate") var searchDate: String? = "",
    @SerializedName("startDate") var startDate: String? = "",
    @SerializedName("endDate") var endDate: String? = "",
    @SerializedName("startIndex") var startIndex: Long? = 0,
    @SerializedName("transactionType") var transactionType: String? = "",
    @SerializedName("plateNumber") val plateNumber: String? = "",
    @SerializedName("count") var count: Long? = 0
)

