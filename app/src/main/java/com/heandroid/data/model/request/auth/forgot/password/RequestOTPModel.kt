package com.heandroid.data.model.request.auth.forgot.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class RequestOTPModel(var optionType: String?,var optionValue: String?) : Parcelable