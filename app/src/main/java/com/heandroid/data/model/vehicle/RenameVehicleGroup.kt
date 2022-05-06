package com.heandroid.data.model.vehicle

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RenameVehicleGroup(
    var groupId: String?,
    var groupName: String?,
) : Parcelable

