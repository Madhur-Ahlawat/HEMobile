package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CardResponseModel(
    val card: Card,
    val check: Check,
    val initiatedBy: InitiatedBy,
    val token: String,
    val tokenType: String,
    val wallet: Wallet
):Parcelable