package com.heandroid.utils

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.heandroid.utils.common.LoginValidateUtils

class LoginFieldValidationTest
{
    @Test
    fun `empty username returns false`(){
        // Pass the value to the function of RegistrationUtil class
        // since RegistrationUtil is an object/ singleton we do not need to create its object
        val result = LoginValidateUtils.validLoginInput(
            "",
            "123",
        )
        // assertThat() comes from the truth library that we added earlier
        // put result in it and assign the boolean that it should return
        assertThat(result).isFalse()
    }

    @Test
    fun `less than two digit password return false`() {
        val result = LoginValidateUtils.validLoginInput(
            "Rahul",
            "a",
        )
        assertThat(result).isTrue()
    }
}