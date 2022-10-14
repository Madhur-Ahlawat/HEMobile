package com.conduent.nationalhighways.data.model.auth.forgot.password

data class ResetPasswordModel(
    var code: String?,
    var referenceId: String?,
    var newPassword: String?,
    var confirmPassword: String?,
    var enable: Boolean?
)