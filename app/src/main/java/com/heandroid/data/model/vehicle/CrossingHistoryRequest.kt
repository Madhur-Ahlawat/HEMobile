package com.heandroid.data.model.vehicle

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


    @SerializedName("searchDate") var searchDate:String="",
    @SerializedName("startDate") var startDate:String="",
    @SerializedName("endDate") var endDate:String="",
    @SerializedName("startIndex") var startIndex:Long=0,
    @SerializedName("transactionType") var transactionType:String="",
    @SerializedName("plateNumber") val plateNumber:String="",
    @SerializedName("count") var count:Long=0
)
