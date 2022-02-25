package com.heandroid.data.model.crossingHistory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CrossingHistoryItem(
    @SerializedName("postingDate") val postingDate: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("tagOrPlateNumber") val tagOrPlateNumber: String,
    @SerializedName("agency") val agency: String,
    @SerializedName("activity") val activity: String,
    @SerializedName("entryTime") val entryTime: String,
    @SerializedName("entryPlaza") val entryPlaza: String,
    @SerializedName("entryLane") val entryLane: String,
    @SerializedName("exitTime") val exitTime: String,
    @SerializedName("exitPlaza") val exitPlaza: String,
    @SerializedName("exitLane") val exitLane: String,
    @SerializedName("vehicleTypeCode") val vehicleTypeCode: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("prepaid") val prepaid: String,
    @SerializedName("planOrRate") val planOrRate: String,
    @SerializedName("fareType") val fareType: String,
    @SerializedName("balance") val balance: String,
    @SerializedName("index") val index: String,
    @SerializedName("exitPlazaName") val exitPlazaName: String,
    @SerializedName("entryPlazaName") val entryPlazaName: String,
    @SerializedName("transactionNumber") val transactionNumber: String,
    @SerializedName("entryDirection") val entryDirection: String,
    @SerializedName("exitDirection") val exitDirection: String,
    @SerializedName("plateNumber") val plateNumber: String

) : Parcelable
