package com.heandroid.data.model.nominatedcontacts

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateAccountRequestModel(
    var firstName: String?,
    var lastName: String?,

    var emailId: String?,

    @SerializedName("phoneNumber", alternate = ["cellPhoneNumber"])
    var phoneNumber: String?,

    val accessType: String? = "",
    val accountId: String?,
    val status: String?,
) : Parcelable
