package com.heandroid.data.model.auth.forgot.password

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConfirmOptionResponseModel(var phone : String?,var email: String?,var statusCode: String?,var message: String?) : Parcelable