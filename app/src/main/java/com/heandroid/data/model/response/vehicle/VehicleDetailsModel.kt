package com.heandroid.data.model.response.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleDetailsModel(
    var vrmNo: String? = null,
    var vrmCountry: String? = null,
    var vrmModel: String?,
    var vrmMake: String? = null,
    var vrmColor: String? = null
) : Parcelable


@Parcelize
data class VehicleTitleAndSub(var title: String? = null, var type: String? = null) : Parcelable