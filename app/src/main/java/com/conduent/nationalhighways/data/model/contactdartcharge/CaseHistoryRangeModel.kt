package com.conduent.nationalhighways.data.model.contactdartcharge

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CaseHistoryRangeModel(
    var startDate: String? = null,
    var endDate: String? = null,
    var status: String? = null,
    var caseNumber: String? = null
) : Parcelable
