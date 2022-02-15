package com.heandroid.model.crossingHistory.request

import com.google.gson.annotations.SerializedName

data class CrossingHistoryRequest (
    /**
     * searchDate": "Transaction Date",

    "startDate": "11/01/2021",

    "endDate": "11/30/2021",

    "startIndex": "1",

    "transactionType": "Toll_Transaction",

    "plateNumber": "87UK836",

    "count": "2"
     */


    @SerializedName("searchDate") val searchDate:String="",
    @SerializedName("startDate") val startDate:String="",
    @SerializedName("endDate") val endDate:String="",
    @SerializedName("startIndex") var startIndex:String="",
    @SerializedName("transactionType") var transactionType:String="",
    @SerializedName("plateNumber") val plateNumber:String="",
    @SerializedName("count") var count:String=""
)
