package com.heandroid.data.model.account

import com.google.gson.annotations.SerializedName


data class AccountInformation (

	@SerializedName("status") val status : String,
	@SerializedName("number") val number : String,
	@SerializedName("type") val type : String,
	@SerializedName("openViolationCount") val openViolationCount : Int,
	@SerializedName("challengeQuestion") val challengeQuestion : String,
	@SerializedName("challengeAnswer") val challengeAnswer : String,
	@SerializedName("password") val password : String
)