package com.heandroid.data.model.request.auth.forgot.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfirmOptionModel(var identifier: String?,var zipCode: String?,var enable: Boolean): Parcelable