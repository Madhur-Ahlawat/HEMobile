package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleInfoDetails(
    val retrievePlateInfoDetails: RetrievePlateInfoDetails
) : Parcelable

@Parcelize
class RetrievePlateInfoDetails(
    val plateNumber: String?,
    val vehicleClass: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehicleColor: String?
) : Parcelable {
}
