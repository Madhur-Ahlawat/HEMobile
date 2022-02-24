package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SetNewPasswordRequest(
    val code : String?,
    val referenceId : String?,
    val newPassword : String?,

):Serializable {

}
