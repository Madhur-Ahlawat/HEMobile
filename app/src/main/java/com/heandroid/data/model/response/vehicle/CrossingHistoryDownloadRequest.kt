package com.heandroid.data.model.response.vehicle

import com.google.gson.annotations.SerializedName

data class CrossingHistoryDownloadRequest(

    @SerializedName("searchDate") var searchDate: String = "",
    @SerializedName("startDate") var startDate: String = "",
    @SerializedName("endDate") var endDate: String = "",
    @SerializedName("startIndex") var startIndex: String = "0",
    @SerializedName("transactionType") var transactionType: String = "",
    @SerializedName("plateNumber") val plateNumber: String = "",
    @SerializedName("downloadType") var downloadType: String = ""

)
