package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewVehicleInfoDetails(

	var vehicleColor: String? = null,
	var plateCountry: String? = null,
	var isExempted: String? = null,
	var vehicleClass: String? = null,
	var vehicleModel: String? = null,
	var plateNumber: String? = null,
	var vehicleMake: String? = null,
	var isRUCEligible: String? = null,
	var isDblaAvailable: Boolean? = true,
	var isUK: Boolean? = true
) : Parcelable


