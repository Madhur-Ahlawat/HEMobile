package com.heandroid.data.model.notification

import com.google.gson.annotations.SerializedName

data class  AlertMessage(
    val viewType: Int = 0,
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
    var isRead: Boolean = false,
    var iSel: Boolean = false
)
