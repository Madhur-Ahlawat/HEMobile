package com.conduent.nationalhighways.data.model.payment

import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse

data class MakeOffPaymentCrossingModel(
    var expand: Boolean?,
    var quantity: Int?,
    var price: Double?,
    var data: VehicleResponse?
)