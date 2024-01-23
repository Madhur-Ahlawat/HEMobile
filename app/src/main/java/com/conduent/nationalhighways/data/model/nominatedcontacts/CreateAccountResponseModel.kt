package com.conduent.nationalhighways.data.model.nominatedcontacts

data class CreateProfileDetailModelModel(
    val success: Boolean?,
    val statusCode: String?,
    val message: String?,
    val mailStatusCode: String?,
    val emailMessage: String?,
    val secondaryAccountId: String?
)

