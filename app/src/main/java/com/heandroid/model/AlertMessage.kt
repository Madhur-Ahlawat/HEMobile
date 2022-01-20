package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class AlertMessage(
    @SerializedName("messageType") val messageType: String,
    @SerializedName("category") val category: String,
    @SerializedName("subCategory") val subCategory: String,
    @SerializedName("createTs") val createTs: String,
    @SerializedName("updateTs") val updateTs: String,
    @SerializedName("startTs") val startTs: String,
    @SerializedName("endTs") val endTs: String,
    @SerializedName("isViewed") val isViewed: String,
    @SerializedName("isReminderReq") val isReminderReq: String,
    @SerializedName("message") val message: String,
    @SerializedName("messageId") val messageId: Int,
    @SerializedName("cscLookUpKey") val cscLookUpKey: String,

    ) {

}
