package com.conduent.nationalhighways.data.model.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountDetails(
    val accountInformation: AccountInformation?,
    val financialInformation: FinancialInformation?,
    val replenishmentInformation: ReplenishmentInformation?,
    val personalInformation: PersonalInformation?
) : Parcelable
