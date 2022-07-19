package com.heandroid.ui.checkpaidcrossings

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class CheckPaidOptionsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test check paid options screen, navigate to used charges screen`() {
        launchFragmentInHiltContainer<CheckPaidOptionsFragment> {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkChargesOption)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rl_un_used_crossings)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_used_crossings)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            assertEquals(
                navController.currentDestination?.id,
                R.id.usedCharges
            )
        }
    }

    @Test
    fun `test check paid options screen, navigate to un used charges screen`() {
        launchFragmentInHiltContainer<CheckPaidOptionsFragment> {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkChargesOption)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rl_used_crossings)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_un_used_crossings)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            assertEquals(
                navController.currentDestination?.id,
                R.id.unUsedCharges
            )
        }
    }
}