package com.heandroid.ui.payment

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class MakeOneOffPaymentFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test one off payment first screen visibility`() {
        launchFragmentInHiltContainer<MakeOneOffPaymentFragment> {
            onView(withId(R.id.charges_crossing_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test one off payment first screen navigation to next screen`() {
        launchFragmentInHiltContainer<MakeOneOffPaymentFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.charges_crossing_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

//            Mockito.verify(navController).navigate(R.id.action_makeOneOffPaymentFragment_to_makePaymentAddVehicleFragment, bun)

        }
    }
}