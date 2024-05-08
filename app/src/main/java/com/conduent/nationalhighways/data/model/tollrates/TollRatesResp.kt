package com.conduent.nationalhighways.data.model.tollrates

data class TollRatesResp(
    val vehicleId: Int?,
    val vehicleType: String?,
    val videoRate: Double?,
    val etcRate: Double?,
    val vehicleTypeDesc:String?
)
