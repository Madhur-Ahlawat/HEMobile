package com.heandroid.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("client_id")
    var clientId: String,

    @SerializedName("grant_type")
    var grantType: String,

    @SerializedName("client_secret")
    var clientSecret: String,

    @SerializedName("agencyID")
    var agecyId: Int,

    @SerializedName("value")
    var value: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("validatePasswordCompliance")
    var validatePasswordCompliance: String
)