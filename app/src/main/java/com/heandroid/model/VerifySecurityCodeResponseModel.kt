package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class VerifySecurityCodeResponseModel(
    @SerializedName("successful") val successful : Boolean
) {

}
