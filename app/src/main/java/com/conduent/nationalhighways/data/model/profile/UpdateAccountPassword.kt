package com.conduent.nationalhighways.data.model.profile

data class UpdateAccountPassword(
    var currentPassword: String?,
    var newPassword: String?,
    var confirmPassword: String?
)