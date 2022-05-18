package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InitiatedBy(
    val isTrusted: Boolean
):Parcelable