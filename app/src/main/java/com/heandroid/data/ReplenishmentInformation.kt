package com.heandroid.data

import com.google.gson.annotations.SerializedName
data class ReplenishmentInformation (

	@SerializedName("type") val type : String,
	@SerializedName("automaticReplenishmentThreshold") val automaticReplenishmentThreshold : Int,
	@SerializedName("lastReplenishedDate") val lastReplenishedDate : String,
	@SerializedName("lastReplenishedAmount") val lastReplenishedAmount : Double,
	@SerializedName("suggestedReplenishmentAmount") val suggestedReplenishmentAmount : String
)