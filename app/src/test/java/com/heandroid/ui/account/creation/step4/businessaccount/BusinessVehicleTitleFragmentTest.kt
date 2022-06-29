package com.heandroid.ui.account.creation.step4.businessaccount

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
import com.heandroid.utils.common.Constants
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
class BusinessVehicleTitleFragmentTest {

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
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                }
            )
        }
    }

    @Test
    fun `test business vehicle title screen visibility`() {
        launchFragmentInHiltContainer<BusinessVehicleTitleFragment>(bundle) {
            onView(withId(R.id.addTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleDescription)).check(matches(isDisplayed()))
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test business vehicle title screen, navigate to next screen`() {
        launchFragmentInHiltContainer<BusinessVehicleTitleFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleTitleFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.addTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleDescription)).check(matches(isDisplayed()))
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
            )
        }
    }
}