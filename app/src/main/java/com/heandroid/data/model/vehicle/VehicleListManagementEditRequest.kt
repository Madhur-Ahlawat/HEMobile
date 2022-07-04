package com.heandroid.data.model.vehicle

import com.google.gson.annotations.SerializedName
import com.heandroid.databinding.FragmentBusinessVehicleNonUkMakeBinding

data class VehicleListManagementEditRequest(
    var newPlateInfo: PlateInfoResponseManagement?,
    var vehicleInfo: VehicleInfoResponseManagement?
)

data class PlateInfoResponseManagement(
    var number : String? ="",
    var country :String? = "UK",
    var vehicleGroup: String?="",
    var state:String?="",
    var type: String?="STANDARD",
    var vehicleComments: String?="",
)

data class VehicleInfoResponseManagement(
    var make: String? = "",
    var model: String? = "",
    var year: String? = "",
    var rowId: String? = "",
    var typeDescription: String? = "",
    var vehicleClassDesc: String? = "",
    var color: String? = ""
)
