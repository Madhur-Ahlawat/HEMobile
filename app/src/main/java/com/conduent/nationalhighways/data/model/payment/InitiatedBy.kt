package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InitiatedBy(
    val isTrusted: Boolean?
) : Parcelable