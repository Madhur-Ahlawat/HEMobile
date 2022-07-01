package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ChooseOptionForgotFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.OPTIONS,
                ConfirmOptionResponseModel(
                    "", "", "",
                    "", ""
                )
            )
        }
    }

    @Test
    fun `test choose option for forgot email screen visibility`() {
        launchFragmentInHiltContainer<ChooseOptionForgotFragment>(bundle) {
            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.email_radio_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.text_message_radio_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.post_mail_radio_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test choose option for forgot email screen, navigate to next screen`() {
        launchFragmentInHiltContainer<ChooseOptionForgotFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.chooseOptionFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<TextView>(R.id.btnOk)?.performClick()
            }
            onView(withId(R.id.post_mail_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.email_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.text_message_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.otpFragment
            )
        }
    }

    @Test
    fun `test choose option for forgot email screen, navigate to next screen with post mail`() {
        launchFragmentInHiltContainer<ChooseOptionForgotFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.chooseOptionFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.email_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.text_message_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.post_mail_radio_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.postalEmailFragment
            )
        }
    }


}