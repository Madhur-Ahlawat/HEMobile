package com.heandroid.data.model.accountpayment

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class AccountGetThresholdResponse(
    @SerializedName("thresholdAmountVo") val thresholdAmountVo: ThresholdAmountValue?,
    @SerializedName("statusCode") val statusCode: String?,
    @SerializedName("message") val message: String?,
) : Parcelable

@Parcelize
data class ThresholdAmountValue(
    @SerializedName("thresholdAmount") val thresholdAmount: String?,
    @SerializedName("customerAmount") val customerAmount: String?,
    @SerializedName("suggestedAmount") val suggestedAmount: String?,
    @SerializedName("suggestedThresholdAmount") val suggestedThresholdAmount: String?,
    ) : Parcelable
