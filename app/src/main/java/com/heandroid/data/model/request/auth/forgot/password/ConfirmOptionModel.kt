package com.heandroid.data.model.request.auth.forgot.password

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmOptionModel(var accountNumber: String?,var email: String?,var phone: String?,var enable: Boolean): Parcelable