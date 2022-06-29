package com.heandroid.ui.account.creation.step2.businessaccount

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
class BusinessInfoFragmentTest {

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

    @After
    fun tearDown() {

    }

    @Test
    fun `test business info screen visibility`() {
        launchFragmentInHiltContainer<BusinessInfoFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.personal_account)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test business info screen navigation to next screen`() {
        launchFragmentInHiltContainer<BusinessInfoFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessInfoFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.personal_account)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            assertEquals(navController.currentDestination?.id, R.id.businessPrepayInfoFragment)
            val currentDestinationArgs = navController.backStack.last().arguments
            assertTrue(BaseActions.equalBundles(currentDestinationArgs, bundle))
        }
    }

    @Test
    fun `test business info screen navigation to next screen, for edit account type flow`() {
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<BusinessInfoFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessInfoFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.personal_account)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_business)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            assertEquals(navController.currentDestination?.id, R.id.businessPrepayInfoFragment)
            val currentDestinationArgs = navController.backStack.last().arguments
            assertTrue(BaseActions.equalBundles(currentDestinationArgs, bundle))
        }
    }
}