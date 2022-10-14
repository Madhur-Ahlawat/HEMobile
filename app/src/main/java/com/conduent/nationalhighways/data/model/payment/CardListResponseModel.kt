package com.conduent.nationalhighways.data.model.payment

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
    val primaryCard: Boolean?,
    val rowId: String?,
    val state: String?,
    val zipCode: String?
)