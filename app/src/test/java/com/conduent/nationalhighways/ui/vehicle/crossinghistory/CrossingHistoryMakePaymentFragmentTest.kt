package com.conduent.nationalhighways.ui.vehicle.crossinghistory

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@MediumTest
class CrossingHistoryMakePaymentFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test crossing history details screen for paid payment `() {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, DataFile.getCrossingHistoryItem("1234"))
        }
        launchFragmentInHiltContainer<CrossingHistoryMakePaymentFragment>(bundle) {
            onView(withId(R.id.make_payment_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.back_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_date)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_time)).check(matches(isDisplayed()))
            onView(withId(R.id.direction)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.transaction_id)).check(matches(isDisplayed()))
            onView(withId(R.id.status)).check(matches(isDisplayed()))
            onView(withText("Download payment receipt")).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test crossing history details screen for un-paid payment `() {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, DataFile.getCrossingHistoryItem("1234", "N"))
        }
        launchFragmentInHiltContainer<CrossingHistoryMakePaymentFragment>(bundle) {
            onView(withId(R.id.make_payment_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.back_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_date)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_time)).check(matches(isDisplayed()))
            onView(withId(R.id.direction)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.transaction_id)).check(matches(isDisplayed()))
            onView(withId(R.id.status)).check(matches(isDisplayed()))
            onView(withText("Make payment")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test crossing history details screen, cancel button`() {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, DataFile.getCrossingHistoryItem("1234"))
        }
        launchFragmentInHiltContainer<CrossingHistoryMakePaymentFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.make_payment_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.back_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_date)).check(matches(isDisplayed()))
            onView(withId(R.id.crossing_time)).check(matches(isDisplayed()))
            onView(withId(R.id.direction)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.transaction_id)).check(matches(isDisplayed()))
            onView(withId(R.id.status)).check(matches(isDisplayed()))
            onView(withText("Download payment receipt")).check(matches(isDisplayed()))

            onView(withId(R.id.back_btn)).perform(ViewActions.click())
            Mockito.verify(navController).popBackStack()
        }
    }
}