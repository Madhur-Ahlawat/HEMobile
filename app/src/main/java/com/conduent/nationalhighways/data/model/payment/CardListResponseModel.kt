package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardListResponseModel(
    val addressLine1: String?,
    val bankAccount: Boolean?,
    var check : Boolean=false,
    val bankAccountNumber: String?,
    val bankAccountType: String?,
    val bankRoutingNumber: String?,
    val cardType: String?,
    var cardNumber: String?,
    val city: String?,
    val country: String?,
    val customerVaultId: String?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val paymentSeqNumber: Int?,
    var primaryCard: Boolean?,
    val rowId: String?,
    val state: String?,
    val zipCode: String?,
    var isSelected:Boolean=false,
    val emandateStatus:String="",
    val expMonth:String="",
    val expYear:String=""

) : Parcelable
