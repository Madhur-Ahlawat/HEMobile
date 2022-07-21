package com.heandroid.ui.account.profile.password

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.utils.common.Constants
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ProfilePasswordSuccessfulFragmentTest {

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
            putBoolean(Constants.UPDATE_PIN_FLOW, true)
        }
    }

    @Test
    fun `test profile password success screen visibility`() {
        bundle.putBoolean(Constants.UPDATE_PIN_FLOW, false)
        launchFragmentInHiltContainer<ProfilePasswordSuccessfulFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordSuccessfulFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test profile password success screen, for update pin flow`() {
        launchFragmentInHiltContainer<ProfilePasswordSuccessfulFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordSuccessfulFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.viewProfile
            )
        }
    }
}
