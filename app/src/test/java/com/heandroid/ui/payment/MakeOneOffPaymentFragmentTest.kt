package com.heandroid.ui.payment

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
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
class MakeOneOffPaymentFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test one off payment first screen visibility`() {
        launchFragmentInHiltContainer<MakeOneOffPaymentFragment> {
            onView(withId(R.id.charges_crossing_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test one off payment first screen navigation to next screen`() {
        launchFragmentInHiltContainer<MakeOneOffPaymentFragment> {
            navController.setGraph(R.navigation.nav_graph_make_off_payment)
            navController.setCurrentDestination(R.id.makeOneOffPaymentFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.charges_crossing_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.makePaymentAddVehicleFragment
            )

        }
    }
}