package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NonUKVehicleModel (
    var plateCountry: String?="",
    var plateTypeDesc: String?="",
    var vehicleColor: String?="",
    var vehicleComments: String?="",
    var vehicleMake: String?="",
    var vehicleModel: String?="",
    var vehiclePlate: String?="",
    var vehicleYear: String?="",
    var vehicleClass: String?="",
    var isFromCreateNonVehicleAccount: Boolean? = false,
    var vehicleClassDesc: String?="",
    var vehicleGroup: String?=""
) : Parcelable