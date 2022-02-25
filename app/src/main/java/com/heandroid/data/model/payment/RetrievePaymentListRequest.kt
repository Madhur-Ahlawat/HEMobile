package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class RetrievePaymentListRequest (
    @SerializedName("searchDateType") val searchDateType:String,
    @SerializedName("startDate")val startDate:String, // "12/03/2021",
    @SerializedName("endDate")val endDate:String, // "03/08/2021",
    @SerializedName("transactionType") val transactionTypeval : String,// "PREPAID",
    @SerializedName("sortColumn") val sortColumn:String, // "TX_DATE",
    @SerializedName("startIndex")val startIndex:Int,// 0,
    @SerializedName("numberOfResults") val numberOfResults :Int, // 10,
    @SerializedName("tagNumber") val targetNumber:String,// "123456789",
    @SerializedName("licenseNumber") val licenseNumber:String
)
