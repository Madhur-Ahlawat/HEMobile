package com.heandroid.data.model.createaccount

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailVerificationRequest(
    val selectionType: String?,
    val selectionValues: String?
) : Parcelable
