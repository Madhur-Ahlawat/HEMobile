package com.heandroid.data.model.nominatedcontacts

data class CreateAccountResponseModel(
    val success: Boolean?,
    val statusCode: String?,
    val message: String?,
    val mailStatusCode: String?,
    val emailMessage: String?,
    val secondaryAccountId: String?
)

