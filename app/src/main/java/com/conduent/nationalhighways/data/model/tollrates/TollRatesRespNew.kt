package com.conduent.nationalhighways.data.model.tollrates

data class TollRatesRespNew(
    val vehicleId: Int?,
    val vehicleType: String?,
    val vehicleClass: String?,
    val oneOffPaymentRate: String?,
    val ifYouHaveAccountRate: String?
)
