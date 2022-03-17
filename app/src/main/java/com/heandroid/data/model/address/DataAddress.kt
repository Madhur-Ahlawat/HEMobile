package com.heandroid.data.model.address

import com.google.gson.annotations.SerializedName

data class DataAddress(

    @SerializedName("organisation") var organisation: String = "",
    @SerializedName("poperty") var poperty: String = "",
    @SerializedName("street") var street: String = "",
    @SerializedName("locality") var locality: String = "0",
    @SerializedName("town") var town: String = "",
    @SerializedName("country") val country: String = "",
    @SerializedName("postcode") var postcode: String = ""
)




