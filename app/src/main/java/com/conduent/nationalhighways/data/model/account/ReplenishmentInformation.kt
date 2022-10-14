package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReplenishmentInformation (

	@SerializedName("type") val type : String?,
	@SerializedName("automaticReplenishmentThreshold") val automaticReplenishmentThreshold : Int?,
	@SerializedName("lastReplenishedDate") val lastReplenishedDate : String?,
	@SerializedName("lastReplenishedAmount") val lastReplenishedAmount : Double?,
	@SerializedName("suggestedReplenishmentAmount") val suggestedReplenishmentAmount : String?,
	@SerializedName("tollBalance") val tollBalance: String?,
	@SerializedName ("violationBalance") val violationBalance: String?,
	@SerializedName("currentBalance") val currentBalance: String?,
	@SerializedName("maximumBalance") val maximumBalance: String?,
	@SerializedName("replenishThreshold") val replenishThreshold: String?,
	@SerializedName("replenishAmount") val replenishAmount: String?,
	@SerializedName("reBillPayType") val reBillPayType: String?,
	@SerializedName("cashMode") val cashMode: String?
) : Parcelable