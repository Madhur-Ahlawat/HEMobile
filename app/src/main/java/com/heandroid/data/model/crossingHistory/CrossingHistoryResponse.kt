package com.heandroid.data.model.crossingHistory

import com.google.gson.annotations.SerializedName

data class CrossingHistoryResponse(
    @SerializedName("transaction")
    val transaction: MutableList<CrossingHistoryItem>?,
    @SerializedName("count")
    val count: String?,
)

