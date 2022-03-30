package com.heandroid.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReplenishmentInformation(
    val automaticReplenishmentThreshold: String?,
    val cashMode: String?,
    val currentBalance: String,
    val lastReplenishedAmount: String?,
    val lastReplenishedDate: String,
    val maximumBalance: String,
    val reBillPayType: String,
    val replenishAmount: String,
    val replenishThreshold: String,
    val suggestedReplenishmentAmount: String?,
    val tollBalance: String,
    val type: String?,
    val violationBalance: String
) : Parcelable