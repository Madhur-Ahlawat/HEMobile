package com.conduent.nationalhighways.ui.auth.forgot.password

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
class PostEmailForgotFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test post email forgot screen visibility`() {
        launchFragmentInHiltContainer<PostEmailForgotFragment> {
            onView(withId(R.id.title1)).check(matches(isDisplayed()))
            onView(withId(R.id.title2)).check(matches(isDisplayed()))
            onView(withId(R.id.title3)).check(matches(isDisplayed()))
            onView(withId(R.id.title4)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_accept)).check(matches(isDisplayed()))
        }
    }
}