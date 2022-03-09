package com.heandroid.data.model.nominatedcontacts

import com.google.gson.annotations.SerializedName


data class TerminateRequestModel(
    @SerializedName("accountId") val accountID: String,
    @SerializedName("status") val status: String,
    @SerializedName("cellPhoneNumber") val number: String?
)
