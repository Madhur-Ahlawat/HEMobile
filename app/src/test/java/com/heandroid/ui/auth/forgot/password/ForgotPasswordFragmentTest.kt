package com.heandroid.ui.auth.forgot.password

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
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.ui.loader.ErrorDialog
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
import org.junit.Assert
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
class ForgotPasswordFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ForgotPasswordViewModel>(relaxed = true)

    private val confirmOptionLiveData = MutableLiveData<Resource<ConfirmOptionResponseModel?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test forgot password screen visibility`() {
        launchFragmentInHiltContainer<ForgotPasswordFragment> {
            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.edt_postcode)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test forgot password for success api call`() {
        every { viewModel.confirmOption } returns confirmOptionLiveData
        launchFragmentInHiltContainer<ForgotPasswordFragment> {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.forgotPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_postcode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("11223344"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            confirmOptionLiveData.postValue(
                Resource.Success(
                    ConfirmOptionResponseModel("", "", "", "", "")
                )
            )
            shadowOf(getMainLooper()).idle()
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.chooseOptionFragment
            )
        }
    }

    @Test
    fun `test forgot password for failure api call`() {
        every { viewModel.confirmOption } returns confirmOptionLiveData
        launchFragmentInHiltContainer<ForgotPasswordFragment> {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.forgotPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_postcode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("11223344"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            confirmOptionLiveData.postValue(
                Resource.Success(
                    ConfirmOptionResponseModel("", "", "1054", "", "")
                )
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test forgot password for unknown api call error`() {
        every { viewModel.confirmOption } returns confirmOptionLiveData
        launchFragmentInHiltContainer<ForgotPasswordFragment> {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.forgotPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_postcode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("11223344"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            confirmOptionLiveData.postValue(
                Resource.DataError(
                    "unknown error"
                )
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }
}