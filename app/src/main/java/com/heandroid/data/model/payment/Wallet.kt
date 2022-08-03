package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wallet(
    val billingInfo: BillingInfo? = null,
    val cardDetails: String? = null,
    val cardNetwork: String? = null,
    val email: String? = null,
    val shippingInfo: ShippingInfo? = null
) : Parcelable