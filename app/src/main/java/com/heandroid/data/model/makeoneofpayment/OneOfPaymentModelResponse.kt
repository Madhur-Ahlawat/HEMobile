package com.heandroid.data.model.makeoneofpayment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OneOfPaymentModelResponse(val status: Int?, val refrenceNumber: String?):Parcelable