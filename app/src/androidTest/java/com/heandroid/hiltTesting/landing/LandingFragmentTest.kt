package com.heandroid.hiltTesting.landing

import androidx.test.core.app.launchActivity
import androidx.test.filters.MediumTest
import com.heandroid.ui.startNow.StartNowBaseActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class LandingFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        // to inject any thing
        hiltRule.inject()
    }

    @Test
    fun clickOnCreateAccountButtonClicked() {
        val scenario = launchActivity<StartNowBaseActivity>()
    }


    @After
    fun after() {

    }
}