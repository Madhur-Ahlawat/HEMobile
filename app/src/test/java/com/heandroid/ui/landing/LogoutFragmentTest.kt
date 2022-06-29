package com.heandroid.ui.landing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
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
@MediumTest
class LogoutFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test landing fragment screen visibility`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test landing fragment, landing screen navigation`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test landing fragment, login screen navigation`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

}