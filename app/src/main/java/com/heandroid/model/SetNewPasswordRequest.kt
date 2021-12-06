package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SetNewPasswordRequest(
//{
//    "accountNumber": "string",
//    "code": "string",
//    "newPassword": "string"
//}
    @SerializedName("accountNumber") val accountNumber : String="",
    @SerializedName("code") val code : String="",
    @SerializedName("newPassword") val newPassword : String="",

):Serializable {

}
