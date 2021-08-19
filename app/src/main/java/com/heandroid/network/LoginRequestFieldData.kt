package com.heandroid.network

object LoginRequestFieldData {

    data class LoginBody(
        val clientID: String,
        val grantType: String,
        val agecyId: String,
        val clientSecret: String,
        val value: String,
        val password: String,
        val validatePasswordCompliance: String,
    )
}