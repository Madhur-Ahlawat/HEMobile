package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConfirmationOptionsResponseModel(
    //{
    //  "accountNumber": "string",
    //  "email": "string",
    //  "phone": "string"
    //}

    @SerializedName("accountNumber") val accountNumber:String,
    @SerializedName("email") val email:String,
    @SerializedName("phone") val phone:String

):Serializable
