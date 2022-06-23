package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CardResponseModel(
    val card: Card?=null,
    val check: Check?=null,
    val initiatedBy: InitiatedBy?=null,
    val token: String?=null,
    val tokenType: String?=null,
    val wallet: Wallet?=null
):Parcelable