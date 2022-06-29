package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
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
import com.heandroid.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.heandroid.data.model.auth.forgot.password.ResetPasswordModel
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.ui.loader.ErrorDialog
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
class CreateNewPasswordFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ForgotPasswordViewModel>(relaxed = true)

    private val forgotPasswordLiveData = MutableLiveData<Resource<ForgotPasswordResponseModel?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                "data",
                SecurityCodeResponseModel("", 0L, "", false)
            )
        }
    }

    @Test
    fun `test reset password screen visibility`() {
        launchFragmentInHiltContainer<CreateNewPasswordFragment>(bundle) {
            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.edt_new_password)).check(matches(isDisplayed()))
            onView(withId(R.id.edt_conform_password)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test reset password for success api call`() {
        every { viewModel.resetPassword } returns forgotPasswordLiveData
        every {
            viewModel.checkPassword(
                ResetPasswordModel(
                    "", "",
                    "password", "password", true
                )
            )
        } returns Pair(true, "")
        launchFragmentInHiltContainer<CreateNewPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.createPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_new_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_conform_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            forgotPasswordLiveData.postValue(
                Resource.Success(
                    ForgotPasswordResponseModel(
                        true, "", ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.resetFragment
            )
        }
    }

    @Test
    fun `test reset password for failure api call`() {
        every { viewModel.resetPassword } returns forgotPasswordLiveData
        every {
            viewModel.checkPassword(
                ResetPasswordModel(
                    "", "",
                    "password", "password", true
                )
            )
        } returns Pair(true, "")
        launchFragmentInHiltContainer<CreateNewPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.createPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_new_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_conform_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            forgotPasswordLiveData.postValue(
                Resource.Success(
                    ForgotPasswordResponseModel(
                        false, "", ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test reset password for unequal passwords`() {
        every { viewModel.resetPassword } returns forgotPasswordLiveData
        every {
            viewModel.checkPassword(
                ResetPasswordModel(
                    "", "",
                    "password", "password", true
                )
            )
        } returns Pair(false, "")
        launchFragmentInHiltContainer<CreateNewPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.createPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_new_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_conform_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test reset password for unknown error api call`() {
        every { viewModel.resetPassword } returns forgotPasswordLiveData
        every {
            viewModel.checkPassword(
                ResetPasswordModel(
                    "", "",
                    "password", "password", true
                )
            )
        } returns Pair(true, "")
        launchFragmentInHiltContainer<CreateNewPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.createPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)

            onView(withId(R.id.enter_details_txt)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_new_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_conform_password)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            forgotPasswordLiveData.postValue(Resource.DataError(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }
}