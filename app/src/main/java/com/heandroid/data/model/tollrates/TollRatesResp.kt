package com.heandroid.data.model.tollrates

data class TollRatesResp(
    val vehicleId: Int,
    val vehicleType: String,
    val videoRate: Double,
    val etcRate: Double
)
