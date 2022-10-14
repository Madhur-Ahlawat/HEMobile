package com.conduent.nationalhighways.data.model.crossingHistory

import com.google.gson.annotations.SerializedName

data class CrossingHistoryApiResponse(
    @SerializedName("transactionList") val transactionList: CrossingHistoryResponse?,
    @SerializedName("statusCode") val statusCode: String?,
    @SerializedName("message") val message: String?
)
