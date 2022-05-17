package com.heandroid.data.model.payment
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(
    val bin: String,
    val exp: String,
    val hash: String,
    val number: String,
    val type: String
):Parcelable