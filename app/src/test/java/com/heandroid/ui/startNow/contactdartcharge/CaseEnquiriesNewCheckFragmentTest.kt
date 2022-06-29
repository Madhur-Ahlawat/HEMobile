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
class CaseEnquiriesNewCheckFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test cases and enquiries screen visibility`() {
        launchFragmentInHiltContainer<CaseEnquiriesNewCheckFragment> {
            Espresso.onView(ViewMatchers.withId(R.id.top_title_txt))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_check_enquiry_status))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_raise_new_enquiry))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun `test cases and enquiries, navigate to next screen for raise new enquiry`() {
        launchFragmentInHiltContainer<CaseEnquiriesNewCheckFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.caseEnquiriesNewCheckFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.top_title_txt))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_check_enquiry_status))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_raise_new_enquiry))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.newCaseCategoryFragment
            )
        }
    }

    @Test
    fun `test cases and enquiries, navigate to next screen`() {
        launchFragmentInHiltContainer<CaseEnquiriesNewCheckFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.caseEnquiriesNewCheckFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.top_title_txt))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_raise_new_enquiry))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rl_check_enquiry_status))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.caseDetailsDartChargeFragment
            )
        }
    }

}