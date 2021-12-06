package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VerifySecurityCodeRequestModel(
        @SerializedName("code") val code: String,
        @SerializedName("otpExpiryInSeconds") val otpExpiryInSeconds: Int,
        @SerializedName("referenceId") val referenceId: String?,
        @SerializedName("successful") val successful: Boolean
        ):Serializable{

}
