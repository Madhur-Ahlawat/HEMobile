package com.heandroid.data.model.response.auth.forgot.password

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConfirmOptionResponseModel(var phone : String?,var email: String?,var statusCode: Int) : Parcelable