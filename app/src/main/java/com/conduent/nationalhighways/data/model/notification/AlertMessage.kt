package com.conduent.nationalhighways.data.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertMessage(
    val viewType: Int = 0,
    @SerializedName("messageType") val messageType: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("subCategory") val subCategory: String?,
    @SerializedName("createTs") val createTs: String?,
    @SerializedName("updateTs") val updateTs: String?,
    @SerializedName("startTs") val startTs: String?,
    @SerializedName("endTs") val endTs: String?,
    @SerializedName("field3") val field3: String?,
    @SerializedName("isViewed") val isViewed: String?,
    @SerializedName("isReminderReq") val isReminderReq: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("messageId") val messageId: Int?,
    @SerializedName("cscLookUpKey") val cscLookUpKey: String?,
    @SerializedName("isDeleted") val isDeleted: String?,
    var isRead: Boolean = false,
    var iSel: Boolean = false,
    var isSelectListItem: Boolean = false,
    var isExpanded: Boolean = false,
    var isSeeMore: Boolean = false
) : Parcelable
