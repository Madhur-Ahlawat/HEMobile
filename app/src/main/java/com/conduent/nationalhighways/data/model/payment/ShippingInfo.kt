package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingInfo(
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val country: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val postalCode: String? = null,
    val state: String? = null
) : Parcelable