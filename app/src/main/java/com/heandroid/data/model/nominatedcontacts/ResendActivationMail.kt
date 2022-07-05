package com.heandroid.data.model.nominatedcontacts

import com.google.gson.annotations.SerializedName


data class ResendActivationMail(
    @SerializedName("ncaccntId") val accountId: String?,
    @SerializedName("reCalcDate") val reDate: String?
)

data class ResendRespModel(
    val message: String?,
    val status: String?
)
