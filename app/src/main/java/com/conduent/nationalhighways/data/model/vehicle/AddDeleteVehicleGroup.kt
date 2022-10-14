package com.conduent.nationalhighways.data.model.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddDeleteVehicleGroup(
    var groupName: String?
) : Parcelable

