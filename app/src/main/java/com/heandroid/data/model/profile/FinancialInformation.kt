package com.heandroid.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinancialInformation(
    val availablePayments: String?,
    val currentBalance: String?,
    val editCardExpMonth: String?,
    val editCardExpYear: String?,
    val editCardFirstName: String?,
    val editCardLastName: String?,
    val editCardMiddleName: String?,
    val editCreditCardNumber: String?,
    val editCreditCardType: String?,
    val financialStatus: String?,
    val maskedNumber: String?,
    val paymentTypeInfo: String?,
    val statementDeliveryInterval: String?,
    val statementDeliveryMethod: String?,
    val tollBalance: String?,
    val violationBalance: String?
) : Parcelable