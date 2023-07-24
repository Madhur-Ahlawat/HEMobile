package com.conduent.nationalhighways.data.model.auth.forgot.password

data class ForgotPasswordResponseModel(
    var emailStatusCode: String?,
    var success: Boolean?,
    var message: String?,
    var statusCode: String?
)