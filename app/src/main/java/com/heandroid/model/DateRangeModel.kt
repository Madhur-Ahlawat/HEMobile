package com.heandroid.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateRangeModel(var type : String?,var from : String?,var to : String?,var title: String?) : Parcelable