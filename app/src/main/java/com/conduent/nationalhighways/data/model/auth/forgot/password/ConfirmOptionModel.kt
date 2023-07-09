package com.conduent.nationalhighways.data.model.auth.forgot.password

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfirmOptionModel(
    var identifier: String?,
    var enable: Boolean
) : Parcelable