package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class PlateInfoResponse(
    @SerializedName("number")
    var number: String,
    @SerializedName("country")
    var country: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("type")
    var type: String,
)
