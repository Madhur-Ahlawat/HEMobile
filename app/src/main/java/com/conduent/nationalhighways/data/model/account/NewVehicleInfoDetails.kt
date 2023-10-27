package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewVehicleInfoDetails(
	var vehicleInfo: String? = null,
	var make: String? = null,
	var model: String? = null,
	var year: String? = null,
	var typeId: String? = null,
	var rowId: String? = null,
	var typeDescription: String? = null,
	var vehicleClassDesc: String? = null,
	var effectiveStartDate: String? = null,
	var groupName: String? = null,
	var vehicleColor: String? = null,
	var plateCountry: String? = null,
	var isExempted: String? = null,
	var vehicleClass: String? = null,
	var vehicleModel: String? = null,
	var plateNumber: String? = null,
	var vehicleMake: String? = null,
	var isRUCEligible: String? = null,
	var isDblaAvailable: Boolean? = true,
	var isUK: Boolean? = true,
	var status: Boolean? = false,
) : Parcelable


