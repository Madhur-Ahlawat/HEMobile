package com.conduent.nationalhighways.data.model.vehicle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RenameVehicleGroup(
    var groupId: String?,
    var groupName: String?,
) : Parcelable

