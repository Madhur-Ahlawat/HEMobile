package com.heandroid.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetSecurityCodeRequestModel(

    @SerializedName("accountNumber") val accountNumber : String,
    @SerializedName("optionType") val optionType : String,
    @SerializedName("optionValue") val optionValue : String

) :Serializable{

}
