package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleInfoDetails(
    var retrievePlateInfoDetails: RetrievePlateInfoDetails?
) : Parcelable

@Parcelize
data class RetrievePlateInfoDetails(
    var plateNumber: String?,
    var vehicleClass: String?,
    var vehicleMake: String?,
    var vehicleModel: String?,
    var vehicleColor: String?,

) : Parcelable
