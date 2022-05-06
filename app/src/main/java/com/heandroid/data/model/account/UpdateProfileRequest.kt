package com.heandroid.data.model.account

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(

    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String?=null,
    @SerializedName("addressLine1") val addressLine1: String?=null,
    @SerializedName("addressLine2") val addressLine2: String?=null,
    @SerializedName("city") val city: String?=null,
    @SerializedName("state") val state: String?=null,
    @SerializedName("zipCode") val zipCode: String?=null,
    @SerializedName("zipCodePlus") val zipCodePlus: String?=null,
    @SerializedName("country") val country: String?=null,
    @SerializedName("emailAddress") val emailAddress: String?=null,
    @SerializedName("primaryEmailStatus") val primaryEmailStatus: String?=null, // PENDING
    @SerializedName("primaryEmailUniqueID") val primaryEmailUniqueID: String?=null,
    @SerializedName("secondaryEmail") val secondaryEmail: String?=null,
    @SerializedName("secondaryEmailStatus") val secondaryEmailStatus: String?=null, // PENDING
    @SerializedName("secondaryEmailUniqueID") val secondaryEmailUniqueID: String?=null,
    @SerializedName("phoneCell") val phoneCell: String?=null,
    @SerializedName("phoneDay") val phoneDay: String?=null,
    @SerializedName("phoneFax") val phoneFax: String?=null,
    @SerializedName("smsOption") val smsOption: String?=null,
    @SerializedName("phoneEvening") val phoneEvening: String?=null,
    @SerializedName("emailSwap") val emailSwap: String?=null,
    @SerializedName("referenceId") val referenceId: String?=null,
    @SerializedName("securityCode") val securityCode: String?=null,
    @SerializedName("businessName") val businessName: String?=null,
    @SerializedName("fein") var fein: String?=null,

    )
