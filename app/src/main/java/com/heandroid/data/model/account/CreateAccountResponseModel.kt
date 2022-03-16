package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountResponseModel(
    val accountNumber: String?,
    val emailStatusCode: String?,
    val message: String?,
    val statusCode: String?
) : Parcelable