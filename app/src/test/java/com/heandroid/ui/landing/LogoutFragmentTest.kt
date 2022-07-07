package com.heandroid.ui.landing

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.utils.BaseActions
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
class LogoutFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test landing fragment screen visibility`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test landing fragment, landing screen navigation`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            navController.setGraph(R.navigation.nav_graph_landing)
            navController.setCurrentDestination(R.id.logoutFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.startNow
            )
        }
    }

    @Test
    fun `test landing fragment, login screen navigation`() {
        launchFragmentInHiltContainer<LogoutFragment> {
            onView(withId(R.id.btnStart)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSignin)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

}