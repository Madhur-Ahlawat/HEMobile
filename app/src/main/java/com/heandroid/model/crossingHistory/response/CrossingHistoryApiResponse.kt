package com.heandroid.model.crossingHistory.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CrossingHistoryApiResponse(
    @SerializedName("transactionList") val transactionList: CrossingHistoryResponse,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("message") val message: String
)
