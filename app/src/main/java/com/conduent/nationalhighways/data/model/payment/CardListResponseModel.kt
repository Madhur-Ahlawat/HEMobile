package com.conduent.nationalhighways.data.model.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardListResponseModel(
    var rowId: String = "",
    var bankAccount: Boolean?,
    var bankAccountType: String = "",
    var bankAccountNumber: String = "",
    var paymentSeqNumber: Int?,
    var firstName: String = "",
    var lastName: String = "",
    var primaryCard: Boolean?,
    var customerVaultId: String = "",
    var addressLine1: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var country: String = "",
    var emandateStatus: String = "",

    var check: Boolean = false,
    var bankRoutingNumber: String = "",
    var cardType: String = "",
    var cardNumber: String = "",
    var middleName: String = "",
    var isSelected: Boolean = false,
    var isValidated: Boolean = false,
    var expMonth: String = "",
    var expYear: String = ""

) : Parcelable
