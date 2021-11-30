package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class ForgotUsernameRequest(
    @SerializedName("accountNumber") val accountNumber:String,
    @SerializedName("zipCode") val zipCode:String) {
}