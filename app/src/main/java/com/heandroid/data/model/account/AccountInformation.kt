package com.heandroid.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.heandroid.data.model.communicationspref.CommunicationPrefsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountInformation(
    @SerializedName("status") val status: String?,
    @SerializedName("number") val number: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("openViolationCount") val openViolationCount: Int?,
    @SerializedName("challengeQuestion") val challengeQuestion: String?,
    @SerializedName("challengeAnswer") val challengeAnswer: String?,
    @SerializedName("memberSince") val memberSince: String?,
    @SerializedName("fee") val fee: String?,
    @SerializedName("deliveryType") val deliveryType: String?,
    @SerializedName("oldDeliveryType") val oldDeliveryType: String?,
    @SerializedName("parkingOption") val parkingOption: String?,
    @SerializedName("mdxEnrollmentReadOnlyFlag") val mdxEnrollmentReadOnlyFlag: String?,
    @SerializedName("mdxEnrollmeneWriteFlag") val mdxEnrollmeneWriteFlag: String?,
    @SerializedName("marketOption") val marketOption: String?,
    @SerializedName("ananymous") val ananymous: String?,
    @SerializedName("closeAccount") val closeAccount: String?,
    @SerializedName("parkingPlusAllowed") val parkingPlusAllowed: String?,
    @SerializedName("vehicleListSearch") val vehicleListSearch: String?,
    @SerializedName("vehicleListCount") val vehicleListCount: String?,
    @SerializedName("transponderListSearch") val transponderListSearch: String?,
    @SerializedName("transponderListCount") val transponderListCount: String?,
    @SerializedName("showSuspendedInfo") val showSuspendedInfo: String?,
    @SerializedName("transactionSearchLimit") val transactionSearchLimit: String?,
    @SerializedName("stmtDelivaryMethod") val stmtDelivaryMethod: String?,
    @SerializedName("accountType") val accountType: String?,
    @SerializedName("minPaymentBalance") val minPaymentBalance: String?,
    @SerializedName("securityPin") val securityPin: String?,
    @SerializedName("accountStatus") val accountStatus: String?,
    @SerializedName("accountFinancialstatus") val accountFinancialstatus: String?,
    @SerializedName("challengeQuestionTwo") val challengeQuestionTwo: String?,
    @SerializedName("challengeAnswerTwo") val challengeAnswerTwo: String?,
    @SerializedName("challengeQuestionThree") val challengeQuestionThree: String?,
    @SerializedName("challengeAnswerThree") val challengeAnswerThree: String?,
    @SerializedName("languagePref") val languagePref: String?,
    @SerializedName("communicationPreferences") val communicationPreferences: ArrayList<CommunicationPrefsModel>? = null,
    @SerializedName("paymentTypeInfo") val paymentTypeInfo: String?,
    @SerializedName("stmtDelivaryType") val stmtDelivaryType: String?
) : Parcelable