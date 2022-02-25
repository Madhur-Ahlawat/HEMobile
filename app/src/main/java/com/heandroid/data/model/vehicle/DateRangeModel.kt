package com.heandroid.data.model.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateRangeModel(
    var type: String?,
    var from: String?,
    var to: String?,
    var title: String?
) : Parcelable