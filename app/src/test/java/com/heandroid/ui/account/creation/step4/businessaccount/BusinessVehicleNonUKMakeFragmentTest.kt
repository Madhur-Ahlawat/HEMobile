package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.ui.account.creation.step5.businessaccount.BusinessVehicleNonUKMakeFragment
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.data.DataFile
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
class BusinessVehicleNonUKMakeFragmentTest {

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
    fun `test business vehicle non uk make screen visibility`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKMakeFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleErrorParent)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleRegNum)).check(matches(isDisplayed()))
            onView(withId(R.id.makeInputLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.colorInputLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.modelInputLayout)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test business vehicle non uk screen, navigate to next screen`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKMakeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKMakeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleErrorParent)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.makeInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.modelInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.colorInputEditText)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }

            onView(withId(R.id.nextBtnBusiness)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessNonUKClassFragment
            )
        }
    }
}