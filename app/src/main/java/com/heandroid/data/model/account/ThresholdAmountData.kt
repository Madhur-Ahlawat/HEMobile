package com.heandroid.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThresholdAmountData(
    @SerializedName("thresholdAmount") val thresholdAmount: String,
    @SerializedName("customerAmount") val customerAmount: String,
) : Parcelable
