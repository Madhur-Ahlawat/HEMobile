package com.conduent.nationalhighways.hiltTesting.login

import androidx.test.core.app.launchActivity
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AuthTestActivity {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loadActivity() {
        launchActivity<AuthActivity>()
    }


}