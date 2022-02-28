package com.heandroid.auth.login

import androidx.test.core.app.launchActivity
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.auth.login.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AuthTestActivity {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun loadActivity() {
        val activity = launchActivity<AuthActivity>()
    }



}