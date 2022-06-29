package com.heandroid.ui.startNow.contactdartcharge

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.utils.BaseActions
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
class ProvideDetailsDartChargeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test case details provide screen visibility`() {
        launchFragmentInHiltContainer<ProvideDetailsDartChargeFragment> {
            Espresso.onView(ViewMatchers.withId(R.id.tvProvideDetails))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etFistName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etEmail))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etTelePhone))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun `test case details provide screen, navigate to next screen`() {
        launchFragmentInHiltContainer<ProvideDetailsDartChargeFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.provideDetailsDartChargeFragment)
            Navigation.setViewNavController(requireView(), navController)

            runTest {
                Espresso.onView(ViewMatchers.withId(R.id.etFistName))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                Espresso.onView(ViewMatchers.withId(R.id.etEmail))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.caseEnquiriesNewCheckFragment
            )
        }
    }
}