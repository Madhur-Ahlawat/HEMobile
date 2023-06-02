package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewVehicleInfoDetails(

	val vehicleColor: String? = null,
	val plateCountry: String? = null,
	val isExempted: String? = null,
	val vehicleClass: String? = null,
	val vehicleModel: String? = null,
	val plateNumber: String? = null,
	val vehicleMake: String? = null,
	val isRUCEligible: String? = null
) : Parcelable

/*
@Parcelize
data class NewVehicleInfoDetailsItem(


) : Parcelable
*/
