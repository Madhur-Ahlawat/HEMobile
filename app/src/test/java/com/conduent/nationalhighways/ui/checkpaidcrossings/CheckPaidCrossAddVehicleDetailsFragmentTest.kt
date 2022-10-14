package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class CheckPaidCrossAddVehicleDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putString(Constants.COUNTRY_TYPE, "UK")
            putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, "12345")
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
        }
    }

    @Test
    fun `test paid crossing add vehicle details, navigate to next screen`() {
        launchFragmentInHiltContainer<CheckPaidCrossAddVehicleDetailsFragment>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.addVehicleDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.subTitle)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.makeInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("make"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.modelInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("model"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.colorInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("color"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.next_btn)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.addVehicleClassesFragment
            )
        }
    }
}