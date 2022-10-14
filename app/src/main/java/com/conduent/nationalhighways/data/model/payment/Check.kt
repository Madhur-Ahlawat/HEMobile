package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Check(
    val aba: String?,
    val account: String?,
    val hash: String?,
    val name: String?
) : Parcelable