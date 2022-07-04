package com.heandroid.data.model.vehicle

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleGroupResponse(
    @SerializedName("groupId") var groupId: String?,
    @SerializedName("groupName") var groupName: String?,
    @SerializedName("numberOfVehicles") var numberOfVehicles: String?
) : Parcelable

