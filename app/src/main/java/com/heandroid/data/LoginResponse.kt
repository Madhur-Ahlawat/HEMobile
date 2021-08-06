package com.heandroid.data

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("status_code")
    var statusCode: Int,

    @SerializedName("access_token")
    private var accessToken: String,

    @SerializedName("token_type")
    private var tokenType: String,

    @SerializedName("refresh_token")
    private var refreshToken: String,

    @SerializedName("expires_in")
    private var expiresIn: Int,

    @SerializedName("scope")
    private var scope: String,

    @SerializedName("AgencyId")
    private var agencyID: Int,

    @SerializedName("BrokerId")
    private var brokerID: Int,

    @SerializedName("InternalAgencyId")
    private var internalAgencyID: Int,

    @SerializedName("FirstName")
    private var firstName: String,

    @SerializedName("LastName")
    private var lastName: String,

    @SerializedName("jti")
    private var jti: String
)