package com.conduent.nationalhighways.data.model.auth.forgot.email

data class LoginModel(
    var value: String? = "",
    var password: String? = "",
    val validatePasswordCompliance: String? = "true",
    val enable: Boolean = false
)