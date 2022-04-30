package com.heandroid.data.model.createaccount

data class EmailVerificationResponse(
    val statusCode: String,
    val emailStatusCode: String,
    val message: String,
    val referenceId: String
)
