package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wallet(
    val billingInfo: BillingInfo,
    val cardDetails: String?=null,
    val cardNetwork: String?=null,
    val email: String?=null,
    val shippingInfo: ShippingInfo
):Parcelable