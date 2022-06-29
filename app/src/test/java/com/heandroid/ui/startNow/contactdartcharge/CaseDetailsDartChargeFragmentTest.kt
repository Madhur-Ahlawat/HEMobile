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
class CaseDetailsDartChargeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test case details screen visibility`() {
        launchFragmentInHiltContainer<CaseDetailsDartChargeFragment> {
            Espresso.onView(ViewMatchers.withId(R.id.tvProvideDetails))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etCaseNumber))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnRaiseNewQuery))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun `test case details screen, navigate to next screen for raise enquiry`() {
        launchFragmentInHiltContainer<CaseDetailsDartChargeFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.caseDetailsDartChargeFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.tvProvideDetails))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etCaseNumber))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnRaiseNewQuery))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.newCaseCategoryFragment
            )
        }
    }

    @Test
    fun `test case details screen, navigate to next screen`() {
        launchFragmentInHiltContainer<CaseDetailsDartChargeFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.caseDetailsDartChargeFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.tvProvideDetails))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etCaseNumber))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnRaiseNewQuery))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            runTest {
                Espresso.onView(ViewMatchers.withId(R.id.etCaseNumber))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1122334455"))
                Espresso.closeSoftKeyboard()
                delay(500)
                Espresso.onView(ViewMatchers.withId(R.id.etLastName))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.caseHistoryDartChargeFragment
            )
        }
    }

}