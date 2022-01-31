package com.heandroid.model

import java.io.Serializable

data class VehicleInfoResponse(
    val make: String,
    val model: String,
    val year: String,
    val typeId: String? = null,
    val rowId: String,
    val typeDescription: String,
    val color: String,
    val vehicleClassDesc: String,
    val effectiveStartDate: String
) : Serializable

