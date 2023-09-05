package com.conduent.nationalhighways.data.model.account

data class GetPlateInfoResponseModelItem(
    val isExempted: String,
    val isRUCEligible: String,
    val plateCountry: String,
    val plateNumber: String,
    val vehicleClass: String,
    val vehicleColor: String,
    val vehicleMake: String,
    val vehicleModel: String
)