package com.heandroid.data.model.auth.forgot.password

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RequestOTPModel(var optionType: String?,var optionValue: String?) : Parcelable