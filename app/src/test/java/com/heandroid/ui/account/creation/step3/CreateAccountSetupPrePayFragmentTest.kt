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
class CreateAccountSetupPrePayFragmentTest {

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
    fun `test create account set up pre pay screen visibility`() {
        launchFragmentInHiltContainer<CreateAccountSetupPrePayFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.desc1)).check(matches(isDisplayed()))
            onView(withId(R.id.desc2)).check(matches(isDisplayed()))
            onView(withId(R.id.desc3)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account set up pre pay screen, navigate to next screen`() {
        launchFragmentInHiltContainer<CreateAccountSetupPrePayFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutInfoFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutInfoConfirmationFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        transactionAmount = "10.00"
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
}