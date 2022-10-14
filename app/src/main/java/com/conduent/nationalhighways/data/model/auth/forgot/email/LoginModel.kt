package com.conduent.nationalhighways.data.model.auth.forgot.email

data class LoginModel(
    var value: String? = "100312942",
    var password: String? = "Welcome12",
    val validatePasswordCompliance: String? = "true",
    val enable: Boolean = false
)