package com.heandroid.ui.account.creation.step3

import android.os.Bundle
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
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
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
@MediumTest
class CreateAccountTopUpFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel()
            )
        }
    }

    @Test
    fun `test create account top up  screen visibility`() {
        launchFragmentInHiltContainer<CreateAccountTopUpFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvInitialDepositLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.desc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvAutoPopDes)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account top-up  screen, navigate to next screen with yes selection`() {
        launchFragmentInHiltContainer<CreateAccountTopUpFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutInfoConfirmationFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvInitialDepositLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.desc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvAutoPopDes)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbYes)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.clYesDes)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        replenishmentAmount = "05.00"
                        thresholdAmount = "10.00"
                    }
                )
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account top-up  screen, navigate to next screen with no selection`() {
        launchFragmentInHiltContainer<CreateAccountTopUpFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutInfoConfirmationFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.mrbNo)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.clNoDes)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        replenishmentAmount = "10.00"
                        thresholdAmount = "10.00"
                    }
                )
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
}