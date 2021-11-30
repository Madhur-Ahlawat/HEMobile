package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class ForgotUsernameApiResponse(
    @SerializedName("userName") val userName:String,
   ) {
}