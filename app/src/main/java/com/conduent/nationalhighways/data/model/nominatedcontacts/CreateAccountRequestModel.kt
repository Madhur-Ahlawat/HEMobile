package com.conduent.nationalhighways.data.model.nominatedcontacts

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountRequestModel(
    var firstName: String?,
    var lastName: String?,
    var emailId: String?,
    @SerializedName("phoneNumber", alternate = ["cellPhoneNumber"])
    var phoneNumber: String?,
    var accessType: String? = "",
    var accountId: String?,
    var status: String?,
    var phoneNumberCountryCode: String = "+44"
) : Parcelable
