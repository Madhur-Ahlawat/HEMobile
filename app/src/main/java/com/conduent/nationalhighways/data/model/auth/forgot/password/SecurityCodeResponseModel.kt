package com.conduent.nationalhighways.data.model.auth.forgot.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecurityCodeResponseModel(
    var code: String?,
    var otpExpiryInSeconds: Long?,
    var referenceId: String?,
    var successful: Boolean?
) : Parcelable