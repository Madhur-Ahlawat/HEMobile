package com.heandroid.ui.account.creation.step2

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
class CreateAccountTypeFragmentTest {

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
    fun `test create account type screen visibility`() {
        launchFragmentInHiltContainer<CreateAccountTypeFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.rgOptions)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbPersonalAccount)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbBusinessAccount)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account type for personal`() {
        launchFragmentInHiltContainer<CreateAccountTypeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.accountTypeSelectionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.rgOptions)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbPersonalAccount)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            onView(withId(R.id.tvPersonalDesc)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                    }
                )
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.personalTypeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account type for business`() {
        launchFragmentInHiltContainer<CreateAccountTypeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.accountTypeSelectionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.rgOptions)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbBusinessAccount)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            onView(withId(R.id.tvBusinessDesc)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                    }
                )
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.businessInfoFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account type for business, for edit account type`() {
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountTypeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.accountTypeSelectionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.rgOptions)).check(matches(isDisplayed()))
            onView(withId(R.id.mrbBusinessAccount)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            onView(withId(R.id.tvBusinessDesc)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.businessInfoFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
}