package com.heandroid.data.model.createaccount

data class ConfirmEmailRequest(
    val referenceID: String,
    val emailID: String,
    val securityCode: String
)
