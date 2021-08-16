package com.heandroid.data

import com.google.gson.annotations.SerializedName

data class FinancialInformation (

	@SerializedName("financialStatus") val financialStatus : String,
	@SerializedName("statementDeliveryInterval") val statementDeliveryInterval : String,
	@SerializedName("statementDeliveryMethod") val statementDeliveryMethod : String,
	@SerializedName("tollBalance") val tollBalance : Double,
	@SerializedName("violationBalance") val violationBalance : Double,
	@SerializedName("currentBalance") val currentBalance : Double
)