package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateProfileRequest(

    @SerializedName("businessName") var businessName: String? = null,
    @SerializedName("fein") var fein: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("addressLine1") var addressLine1: String? = null,
    @SerializedName("addressLine2") var addressLine2: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("zipCode") var zipCode: String? = null,
    @SerializedName("zipCodePlus") var zipCodePlus: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("emailAddress") var emailAddress: String? = null,
    @SerializedName("primaryEmailStatus") var primaryEmailStatus: String? = null,
    @SerializedName("primaryEmailUniqueID") var primaryEmailUniqueID: String? = null,
    @SerializedName("phoneCell") var phoneCell: String? = null,
    @SerializedName("phoneCellCountryCode") var phoneCellCountryCode: String? = null,
    @SerializedName("phoneDay") var phoneDay: String? = null,
    @SerializedName("phoneDayCountryCode") var phoneDayCountryCode: String? = null,
    @SerializedName("phoneFax") var phoneFax: String? = null,
    @SerializedName("smsOption") var smsOption: String? = null,
    @SerializedName("phoneEvening") var phoneEvening: String? = null,
    @SerializedName("correspDeliveryMode") var correspDeliveryMode: String? = null,
    @SerializedName("correspDeliveryFrequency") var correspDeliveryFrequency: String? = null,
    @SerializedName("mfaEnabled") var mfaEnabled: String? = null,


    @SerializedName("cellPhone") var cellPhone: String? = null,
    @SerializedName("securityCode") var securityCode: String? = null,
    @SerializedName("referenceId") var referenceId: String? = null,

    ) : Parcelable
