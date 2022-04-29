package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountVehicleModel (
    var plateCountry: String?,
    var plateTypeDesc: String?,
    var vehicleColor: String?,
    var vehicleComments: String?,
    var vehicleMake: String?,
    var vehicleModel: String?,
    var vehiclePlate: String?,
    var vehicleYear: String?,
    var vehicleStateType: String?,
) : Parcelable