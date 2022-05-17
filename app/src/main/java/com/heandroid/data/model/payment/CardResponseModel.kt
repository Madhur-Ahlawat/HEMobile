package com.heandroid.data.model.payment

data class CardResponseModel(
    val card: Card,
    val check: Check,
    val initiatedBy: InitiatedBy,
    val token: String,
    val tokenType: String,
    val wallet: Wallet
)