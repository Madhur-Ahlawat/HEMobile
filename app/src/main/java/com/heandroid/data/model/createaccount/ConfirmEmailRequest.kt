package com.heandroid.data.model.createaccount

data class ConfirmEmailRequest(
    val referenceId: Long,
    val emailId: String,
    val securityCode: String
)
