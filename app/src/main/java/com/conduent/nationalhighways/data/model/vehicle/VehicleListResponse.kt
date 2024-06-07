package com.conduent.nationalhighways.data.model.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleListResponse(
    var listOfVehicles: List<VehicleResponse> = ArrayList(),
    var count: Int = 0
) : Parcelable
