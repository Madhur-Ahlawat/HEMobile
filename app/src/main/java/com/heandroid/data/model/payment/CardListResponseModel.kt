package com.heandroid.data.model.payment

data class CardListResponseModel(
    val addressLine1: String,
    val bankAccount: Boolean,
    var check : Boolean,
    val bankAccountNumber: String,
    val bankAccountType: String,
    val bankRoutingNumber: String,
    val city: String,
    val country: String,
    val customerVaultId: String,
    val firstName: String,
    val lastName: String,
    val paymentSeqNumber: Int,
    val primaryCard: Boolean,
    val rowId: String,
    val state: String,
    val zipCode: String
)