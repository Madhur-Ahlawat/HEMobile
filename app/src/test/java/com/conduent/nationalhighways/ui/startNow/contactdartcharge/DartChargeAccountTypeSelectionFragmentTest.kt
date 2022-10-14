package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
class DartChargeAccountTypeSelectionFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test account type selection screen visibility`() {
        launchFragmentInHiltContainer<DartChargeAccountTypeSelectionFragment> {
            Espresso.onView(ViewMatchers.withId(R.id.tv_verification))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rgAccount))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.yes))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.no))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun `test account type selection screen, navigate to next screen with account`() {
        launchFragmentInHiltContainer<DartChargeAccountTypeSelectionFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.dartChargeAccountTypeSelectionFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.tv_verification))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rgAccount))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.yes))
                .perform(BaseActions.forceClick())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.no))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(BaseActions.forceClick())
        }
    }

    @Test
    fun `test account type selection screen, navigate to next screen without account`() {
        launchFragmentInHiltContainer<DartChargeAccountTypeSelectionFragment> {
            navController.setGraph(R.navigation.nav_graph_contact_dart_charge)
            navController.setCurrentDestination(R.id.dartChargeAccountTypeSelectionFragment)
            Navigation.setViewNavController(requireView(), navController)
            Espresso.onView(ViewMatchers.withId(R.id.tv_verification))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.rgAccount))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.yes))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.no))
                .perform(BaseActions.forceClick())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.btnContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.provideDetailsDartChargeFragment
            )
        }
    }
}