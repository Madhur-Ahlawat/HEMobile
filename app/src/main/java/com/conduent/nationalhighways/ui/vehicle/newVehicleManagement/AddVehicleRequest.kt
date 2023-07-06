package com.conduent.nationalhighways.ui.vehicle.newVehicleManagement

data class AddVehicleRequest(
    val plateInfo: PlateInfo? = PlateInfo(),
    val vehicleInfo: VehicleInfo? = VehicleInfo()
)

data class PlateInfo(
    var number: String? = null,
    var country: String? = null,
    var vehicleGroup: String? = null,
    var vehicleComments: String? = null,
    var planName: String? = null,
    var state: String? = null,
    var type: String? = null
)

data class VehicleInfo(
    var color: String? = null,
    var year: Int? = null,
    var effectiveStartDate: String? = null,
    var model: String? = null,
    var typeId: Any? = null,
    var typeDescription: String? = null,
    var make: String? = null,
    var vehicleClassDesc: String? = null
)

