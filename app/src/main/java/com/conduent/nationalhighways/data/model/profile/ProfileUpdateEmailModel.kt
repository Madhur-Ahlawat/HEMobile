package com.conduent.nationalhighways.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProfileUpdateEmailModel(
    var referenceId: String?,
    var securityCode : String?,
    val addressLine1: String?="",
    val addressLine2: String?="",
    val city: String?="",
    val country: String?="",
    var emailAddress: String?,
    val phoneCell: String?="",
    val phoneDay: String?="",
    val phoneEvening: String?="",
    val phoneFax: String?="",
    val primaryEmailStatus: String?,
    val primaryEmailUniqueID: String?,
    val smsOption: String?,
    val state: String?="",
    val zipCode: String?="",
    val zipCodePlus: String?=""
) : Parcelable