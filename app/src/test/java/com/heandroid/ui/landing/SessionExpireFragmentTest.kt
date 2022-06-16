package com.heandroid.ui.landing

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class SessionExpireFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test session expired screen for sign in flow`() {
        bundle = Bundle().apply {
            putBundle(ConstantsTest.TYPE, Bundle().apply {putString(ConstantsTest.TYPE, ConstantsTest.LOGIN)  })
        }
        launchFragmentInHiltContainer<SessionExpireFragment>(bundle) {
            onView(withId(R.id.appCompatCheckedTextView)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
                .check(matches(withSubstring("Select the sign in button to log")))
            onView(withId(R.id.btn)).check(matches(isDisplayed()))
                .check(matches(withSubstring("Sign In")))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test session expired screen for token refresh in flow`() {
        bundle = Bundle().apply {
            putBundle(ConstantsTest.TYPE, Bundle().apply {putString(ConstantsTest.TYPE, ConstantsTest.REFRESH_TOKEN)  })
        }
        launchFragmentInHiltContainer<SessionExpireFragment>(bundle) {
            onView(withId(R.id.appCompatCheckedTextView)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
                .check(matches(withSubstring("Select the start now button to")))
            onView(withId(R.id.btn)).check(matches(isDisplayed()))
                .check(matches(withSubstring("Start again")))
                .perform(ViewActions.click())
        }
    }
}