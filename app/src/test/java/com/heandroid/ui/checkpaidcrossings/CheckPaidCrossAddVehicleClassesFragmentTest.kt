package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
class CheckPaidCrossAddVehicleClassesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, VehicleInfoDetails(null))
            putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, "12345")
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
        }
    }

    @Test
    fun `test paid crossing add vehicle details, navigate to next screen`() {
        launchFragmentInHiltContainer<CheckPaidCrossAddVehicleClassesFragment>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.addVehicleClassesFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classBView)).check(matches(isDisplayed()))
            onView(withId(R.id.classCView)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.classC_RadioButton)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())
            onView(withId(R.id.classB_RadioButton)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())
            onView(withId(R.id.classD_RadioButton)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.classVehicleCheckbox)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            assertEquals(
                navController.currentDestination?.id,
                R.id.checkPaidCrossingChangeVrm
            )
        }
    }

    @Test
    fun `test paid crossing add vehicle details, test cancel button`() {
        launchFragmentInHiltContainer<CheckPaidCrossAddVehicleClassesFragment>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.addVehicleClassesFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            assertNotEquals(
                navController.currentDestination?.id,
                R.id.addVehicleClassesFragment
            )
        }
    }

}
