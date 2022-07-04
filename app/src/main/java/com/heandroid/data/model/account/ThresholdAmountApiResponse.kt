package com.heandroid.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThresholdAmountApiResponse (
    @SerializedName("thresholdAmountVo") val thresholdAmountVo : ThresholdAmountData?,
    @SerializedName("statusCode") val statusCode : String?,
    @SerializedName("message") val message : String?,
) : Parcelable