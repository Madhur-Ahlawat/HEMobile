package com.heandroid.data.model.response.auth.forgot.password

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SecurityCodeResponseModel(var code: String?,var otpExpiryInSeconds: Int?,var referenceId: String?,var successful: Boolean) : Parcelable