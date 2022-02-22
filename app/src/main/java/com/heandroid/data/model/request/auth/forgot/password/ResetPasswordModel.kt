package com.heandroid.data.model.request.auth.forgot.password

data class ResetPasswordModel(var code : String?,var referenceId: String?,var newPassword: String?,var confirmPassword: String?,var enable: Boolean?)