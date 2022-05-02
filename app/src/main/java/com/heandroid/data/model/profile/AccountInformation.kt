package com.heandroid.data.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountInformation(
    val accountFinancialstatus: String?,
    val accountStatus: String?,
    val accountType: String?,
    val anmous: Boolean?,
    val challengeAnswer: String?,
    val challengeAnswerThree: String?,
    val challengeAnswerTwo: String?,
    val challengeQuestion: String?,
    val challengeQuestionThree: String?,
    val challengeQuestionTwo: String?,
    val closeAccount: String?,
    val communicationPreferences: String?,
    val deliveryType: String?,
    val fee: String?,
    val languagePref: String?,
    val marketOption: String?,
    val mdxEnrollmeneWriteFlag: String?,
    val mdxEnrollmentReadOnlyFlag: String?,
    val memberSince: String?,
    val minPaymentBalance: String?,
    val number: String?,
    val oldDeliveryType: String?,
    val openViolationCount: String?,
    val parkingOption: String?,
    val parkingPlusAllowed: String?,
    var password: String?,
    val paymentTypeInfo: String?,
    val securityPin: String?,
    val showSuspendedInfo: String?,
    val smsOption: String?,
    val status: String?,
    val stmtDelivaryInterval: String?,
    val stmtDelivaryMethod: String?,
    val stmtDelivaryType: String?,
    val transactionSearchLimit: String?,
    val transponderListCount: String?,
    val transponderListSearch: String?,
    val type: String?,
    val vehicleListCount: String?,
    val vehicleListSearch: String?,
    val ananymous: Boolean,
    val businessName: String?,
    val fein: String?,  // company reg no.
    val accSubType: String?,
    val ncId: String=""
) : Parcelable



