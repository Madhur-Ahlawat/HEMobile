package com.conduent.nationalhighways.data.model.auth.forgot.email

data class ForgotEmailModel(
    var enable: Boolean?,
    var accountNumber: String?,
    var zipCode: String?
)