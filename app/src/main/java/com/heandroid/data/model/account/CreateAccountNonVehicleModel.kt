package com.heandroid.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountNonVehicleModel (
    var plateCountry: String?="",
    var plateTypeDesc: String?="",
    var vehicleColor: String?="",
    val vehicleComments: String?="",
    var vehicleMake: String?="",
    var vehicleModel: String?="",
    var vehiclePlate: String?="",
    var vehicleYear: String?="",
    var isFromCreateNonVehicleAccount: Boolean? = false
) : Parcelable