package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class PaymentModel(
    @SerializedName("activity") val activity : String,
    @SerializedName("agencyName") val agencyName : String,
    @SerializedName("amount") val amount : Int,
    @SerializedName("balance") val balance : Int,
    @SerializedName("entryDate") val entryDate : String,
    @SerializedName("entryLaneName") val entryLaneName : String,
    @SerializedName("entryPlazaName") val entryPlazaName : String,
    @SerializedName("entryTime") val entryTime : String,
    @SerializedName("exitDate") val exitDate : String,
    @SerializedName("exitLaneName") val exitLaneName : String,
    @SerializedName("exitPlazaName") val exitPlazaName : String,
    @SerializedName("exitTime") val exitTime : String,
    @SerializedName("fareType") val fareType : String,
    @SerializedName("planType") val planType : String,
    @SerializedName("postingDate") val postingDate : String,
    @SerializedName("prepaid") val prepaid : Boolean,
    @SerializedName("transactionDate") val transactionDate : String,
    @SerializedName("transponderNumber") val transponderNumber : String,
    @SerializedName("vehicleTypeId") val vehicleTypeId : String
)
