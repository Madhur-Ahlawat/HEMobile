package com.conduent.nationalhighways.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OneOfPaymentModelResponse(
    val status: Int?,
    val refrenceNumber: String?
) : Parcelable