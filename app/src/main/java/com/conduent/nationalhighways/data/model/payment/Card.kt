package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val bin: String?,
    val exp: String?,
    val hash: String?,
    val number: String?,
    val type: String?
) : Parcelable