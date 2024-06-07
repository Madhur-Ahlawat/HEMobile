package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateProfileDetailModelModel(
    val accountNumber: String?,
    val emailStatusCode: String?,
    val message: String?,
    val statusCode: String?,
    var accountType: String?,
    var referenceNumber: String?
) : Parcelable