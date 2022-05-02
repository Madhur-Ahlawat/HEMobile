package com.heandroid.data.model.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.heandroid.data.model.communicationspref.AvailablePaymentsModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinancialInformation(

    @SerializedName("financialStatus") val financialStatus: String,
    @SerializedName("statementDeliveryInterval") val statementDeliveryInterval: String,
    @SerializedName("statementDeliveryMethod") val statementDeliveryMethod: String,
    @SerializedName("tollBalance") val tollBalance: Double,
    @SerializedName("violationBalance") val violationBalance: Double,
    @SerializedName("currentBalance") val currentBalance: Double,
    @SerializedName("editCardFirstName") val editCardFirstName: String,
    @SerializedName("editCardMiddleName") val editCardMiddleName: String,
    @SerializedName("editCardLastName") val editCardLastName: String,
    @SerializedName("editCreditCardType") val editCreditCardType: String,
    @SerializedName("editCreditCardNumber") val editCreditCardNumber: String,
    @SerializedName("editCardExpMonth") val editCardExpMonth: Int,
    @SerializedName("editCardExpYear") val editCardExpYear: Int,
    @SerializedName("maskedNumber") val maskedNumber: String,
    @SerializedName("paymentTypeInfo") val paymentTypeInfo: String?=null,
    @SerializedName("availablePayments") val availablePayments: ArrayList<AvailablePaymentsModel>? = null

) : Parcelable