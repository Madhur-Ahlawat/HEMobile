package com.heandroid.data.model.createaccount

data class ConfirmEmailRequest(
    val referenceId: String,
    val emailId: String,
    val securityCode: String
)
