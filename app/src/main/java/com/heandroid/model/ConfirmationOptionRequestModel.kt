package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class ConfirmationOptionRequestModel(
    //identifier
    //zipCode
    @SerializedName("identifier") val identifier:String,
    @SerializedName("zipCode") val zipCode:String

)
