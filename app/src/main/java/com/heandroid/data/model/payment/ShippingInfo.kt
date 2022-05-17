package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShippingInfo(
    val address1: String,
    val address2: String,
    val city: String,
    val country: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val postalCode: String,
    val state: String
):Parcelable