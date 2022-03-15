package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateAccountVehicleModel (
    val plateCountry: String?,
    val plateTypeDesc: String?,
    val vehicleColor: String?,
    val vehicleComments: String?,
    val vehicleMake: String?,
    val vehicleModel: String?,
    val vehiclePlate: String?,
    val vehicleYear: String?
) : Parcelable