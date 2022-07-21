package com.heandroid.ui.account.profile.password

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
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.launchFragmentInHiltContainer
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
class NominatedProfilePasswordFragmentTest {

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
        }
    }

    @Test
    fun `test profile password screen, navigate to pin screen`() {
        launchFragmentInHiltContainer<NominatedProfilePasswordFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.nominatedProfilePasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("12345678"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), BaseActions.forceTypeText("12345678"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            assertEquals(
                navController.currentDestination?.id,
                R.id.nominatedPinFragment
            )
        }
    }

}