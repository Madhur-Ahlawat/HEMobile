package com.heandroid.data.model.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDateRangeModel(
    var filterType: String?,
    var startDate: String?,
    var endDate: String?,
    var vehicleNumber : String?
) : Parcelable