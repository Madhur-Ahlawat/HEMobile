package com.heandroid.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileDetailModel(
    val accountInformation: AccountInformation?,
    val financialInformation: FinancialInformation?,
    var personalInformation: PersonalInformation?,
    val replenishmentInformation: ReplenishmentInformation?,
    val errorCode: Long?,
    val message : String?,
    val status : String?,
) : Parcelable