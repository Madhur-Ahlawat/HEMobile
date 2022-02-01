package com.heandroid.model

import java.io.Serializable

data class VehicleInfoResponse(
    var make: String,
    var model: String,
    val year: String,
    val typeId: String? = null,
    val rowId: String,
    val typeDescription: String,
    var color: String,
    var vehicleClassDesc: String,
    val effectiveStartDate: String
) : Serializable

