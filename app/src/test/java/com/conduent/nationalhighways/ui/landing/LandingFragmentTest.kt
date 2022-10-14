package com.conduent.nationalhighways.ui.landing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.BaseActions.forceClick
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
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@MediumTest
class LandingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test landing fragment screen visibility`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbCreateAccount)).check(matches(isDisplayed()))
            onView(withId(R.id.rbMakeOffPayment)).check(matches(isDisplayed()))
            onView(withId(R.id.rbResolvePenalty)).check(matches(isDisplayed()))
            onView(withId(R.id.rbCheckForPaid)).check(matches(isDisplayed()))
            onView(withId(R.id.rbViewCharges)).check(matches(isDisplayed()))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account navigation`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbCreateAccount)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test one off payment navigation`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbMakeOffPayment)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            onView(withId(R.id.rbMakeOffPayment))
                .check(matches(withSubstring("payment for crossings without")))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test penalty charge navigation`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbResolvePenalty)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test paid crossings navigation`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbCheckForPaid)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test view charges navigation`() {
        launchFragmentInHiltContainer<LandingFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rbViewCharges)).check(matches(isDisplayed()))
                .perform(forceClick())

            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(ViewActions.click())
        }
    }

}