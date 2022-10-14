package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ValidVehicleCheckRequest(
    val plateNumber: String?,
    val vehicleCountry: String?,
    val plateTypedesc: String? = "STANDARD",
    val vehicleYear: String? = "2021",
    val vehicleModel: String?,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val iagCode: String? = "2",
    val plateSate: String? = "HE",
    ) : Parcelable