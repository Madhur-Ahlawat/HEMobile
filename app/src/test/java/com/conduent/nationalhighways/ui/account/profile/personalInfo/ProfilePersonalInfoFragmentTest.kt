package com.conduent.nationalhighways.ui.account.profile.personalInfo

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
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
class ProfilePersonalInfoFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.DATA, ProfileDetailModel(
                    null, null, null,
                    null, null, "", ""
                )
            )
            putParcelable(
                Constants.NOMINATED_ACCOUNT_DATA, DataFile.getSecondaryAccountData()
            )

        }
    }

    @Test
    fun `test personal info screen, navigate to next screen`() {
        launchFragmentInHiltContainer<ProfilePersonalInfoFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.personalInfoFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFirstName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("first"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("last"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieEmailId)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("test@gmail.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieEveningNo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("12345678"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.postCodeFragment
            )
        }
    }

    @Test
    fun `test personal info screen, navigate to change email screen`() {
        launchFragmentInHiltContainer<ProfilePersonalInfoFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.personalInfoFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFirstName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("first"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("last"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieEmailId)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("test@gmail.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieEveningNo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("12345678"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChangeEmail)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.emailFragment
            )
        }
    }

}