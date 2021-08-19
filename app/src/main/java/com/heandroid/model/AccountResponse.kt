package com.heandroid.model

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    @SerializedName("accountInformation") val accountInformation : AccountInformation,
    @SerializedName("financialInformation") val financialInformation : FinancialInformation,
    @SerializedName("replenishmentInformation") val replenishmentInformation : ReplenishmentInformation,
    @SerializedName("personalInformation") val personalInformation : PersonalInformation
) 
