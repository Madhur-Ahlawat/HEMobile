package com.heandroid.utils.common

import java.util.regex.Pattern

class UtilityClass {

    companion object
    {
    private val EMAIL = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )
    private const val MIN_PASSWORD_LENGTH = 6

    fun isEmailValid(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            EMAIL.matcher(email).matches()
        }
    }

    fun isPasswordValid(password: String?): Boolean {
        return if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            false
        } else {
            password.trim { it <= ' ' }.length >= MIN_PASSWORD_LENGTH
        }
    }
}

}