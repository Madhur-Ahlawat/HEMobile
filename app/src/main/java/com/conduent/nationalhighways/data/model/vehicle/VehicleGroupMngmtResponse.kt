package com.conduent.nationalhighways.data.model.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleGroupMngmtResponse(
    var success: Boolean?,
    var message: String?,
    var statusCode: String?
) : Parcelable

