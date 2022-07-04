package com.heandroid.data.model.auth.forgot.email

import com.google.gson.annotations.SerializedName

data class ForgotUsernameApiResponse(
    @SerializedName("userName") val userName:String?,
   ) {
}