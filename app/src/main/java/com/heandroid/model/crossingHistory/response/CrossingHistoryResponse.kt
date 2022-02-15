package com.heandroid.model.crossingHistory.response

import com.google.gson.annotations.SerializedName

data class CrossingHistoryResponse(
    @SerializedName("transaction")
    val transaction: List<CrossingHistoryItem>,
    @SerializedName(" count")
    val count: String,
)