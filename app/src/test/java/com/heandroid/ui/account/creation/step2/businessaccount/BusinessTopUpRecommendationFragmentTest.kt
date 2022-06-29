package com.heandroid.ui.account.creation.step2.businessaccount

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
import com.heandroid.utils.common.Constants
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
class BusinessTopUpRecommendationFragmentTest {

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
            putString(Constants.NO_OF_CROSSING_BUSINESS, "100")
            putString(Constants.NO_OF_VEHICLE_BUSINESS, "10")
        }
    }

    @Test
    fun `test business top up recommendation screen visibility`() {
        launchFragmentInHiltContainer<BusinessTopUpRecommendationFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmount)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmountFalls)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpCalculateRecommend)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business_topup))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test business top up recommendation screen visibility, navigate to next screen`() {
        launchFragmentInHiltContainer<BusinessTopUpRecommendationFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessTopUpRecommendataionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmount)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmountFalls)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpCalculateRecommend)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business_topup))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.personalDetailsEntryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        thresholdAmount = (10 * 100).toString()
                        replenishmentAmount = (10 * 100 * 2).toString()
                        transactionAmount = (10 * 100 * 2).toString()
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business top up recommendation screen visibility, navigate to next screen for edit account type`() {
        bundle.apply {
            bundle.putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<BusinessTopUpRecommendationFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessTopUpRecommendataionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmount)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmountFalls)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpCalculateRecommend)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business_topup))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.personalDetailsEntryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        thresholdAmount = (10 * 100).toString()
                        replenishmentAmount = (10 * 100 * 2).toString()
                        transactionAmount = (10 * 100 * 2).toString()
                    }
                )
                putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business top up recommendation screen visibility, navigate to next screen for edit payment key`() {
        bundle.apply {
            bundle.putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT_KEY
            )
        }
        launchFragmentInHiltContainer<BusinessTopUpRecommendationFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessTopUpRecommendataionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmount)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpAmountFalls)).check(matches(isDisplayed()))
            onView(withId(R.id.topUpCalculateRecommend)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business_topup))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.paymentSummaryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        thresholdAmount = (10 * 100).toString()
                        replenishmentAmount = (10 * 100 * 2).toString()
                        transactionAmount = (10 * 100 * 2).toString()
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }


}