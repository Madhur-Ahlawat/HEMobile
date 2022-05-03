package com.heandroid.data.model.vehicle

import android.os.Parcelable
import android.text.BoringLayout
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleGroupMngmtResponse(
    var success: Boolean?,
    var message: String?,
    var statusCode: String?
) : Parcelable

