package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class PersonalInformation (

	@SerializedName("userName") val userName : String,
	@SerializedName("title") val title : String,
	@SerializedName("firstName") val firstName : String,
	@SerializedName("middleInitial") val middleInitial : String,
	@SerializedName("lastName") val lastName : String,
	@SerializedName("suffix") val suffix : String,
	@SerializedName("addressLine1") val addressLine1 : String,
	@SerializedName("addressLine2") val addressLine2 : String,
	@SerializedName("city") val city : String,
	@SerializedName("state") val state : String,
	@SerializedName("zipCode") val zipCode : String,
	@SerializedName("zipCodePlus") val zipCodePlus : String,
	@SerializedName("country") val country : String,
	@SerializedName("daytimePhone") val daytimePhone : String,
	@SerializedName("cellPhone") val cellPhone : String,
	@SerializedName("eveningPhone") val eveningPhone : String,
	@SerializedName("fax") val fax : String,
	@SerializedName("emailAddress") val emailAddress : String,
	@SerializedName("mobileAlerts") val mobileAlerts : String,
	@SerializedName("surveyOptIn") val surveyOptIn : String,
	@SerializedName("vrAlexaTc") val vrAlexaTc : Boolean,
	@SerializedName("vrGoogleTc") val vrGoogleTc : Boolean,
	@SerializedName("vrSmsTc") val vrSmsTc : Boolean,
	@SerializedName("pushNotifications") val pushNotifications : Boolean,
	@SerializedName("isNixeAddress") val isNixeAddress : String
)