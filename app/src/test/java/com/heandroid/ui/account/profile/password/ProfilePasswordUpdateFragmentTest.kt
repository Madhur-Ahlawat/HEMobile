package com.heandroid.ui.account.profile.password

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
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.data.model.profile.UpdateAccountPassword
import com.heandroid.data.model.profile.UpdatePasswordResponseModel
import com.heandroid.ui.account.profile.ProfileViewModel
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
class ProfilePasswordUpdateFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ProfileViewModel>(relaxed = true)

    private val updatePasswordLiveData = MutableLiveData<Resource<UpdatePasswordResponseModel?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.updatePassword } returns updatePasswordLiveData
        every {
            viewModel.checkPassword(
                UpdateAccountPassword(
                    "12345", "12345", "12345"
                )
            )
        } returns Pair(true, "")
        bundle = Bundle().apply {
            putParcelable(
                Constants.DATA, ProfileUpdateEmailModel(
                    "", "", "",
                    "", "", "", "", "",
                    "", "", "", "", "",
                    "", "", "", ""
                )
            )
        }
    }

    @Test
    fun `test profile screen update password for incomplete data`() {
        every {
            viewModel.checkPassword(
                UpdateAccountPassword(
                    "12345", "12345", "12345"
                )
            )
        } returns Pair(false, "test message")
        launchFragmentInHiltContainer<ProfilePasswordUpdateFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieCurrentPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChange)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test profile screen update password for success api call`() {
        launchFragmentInHiltContainer<ProfilePasswordUpdateFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieCurrentPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChange)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            shadowOf(getMainLooper()).idle()
            updatePasswordLiveData.postValue(
                Resource.Success(
                    UpdatePasswordResponseModel(
                        "", "", ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals(
                navController.currentDestination?.id,
                R.id.updatePasswordSuccessfulFragment
            )
        }
    }

    @Test
    fun `test profile screen update password for unknown error api call`() {
        launchFragmentInHiltContainer<ProfilePasswordUpdateFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieCurrentPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChange)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            shadowOf(getMainLooper()).idle()
            updatePasswordLiveData.postValue(
                Resource.Success(
                    UpdatePasswordResponseModel(
                        "500", "", ""
                    )
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
    fun `test profile screen update password for failed error api call`() {
        launchFragmentInHiltContainer<ProfilePasswordUpdateFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.updatePasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieCurrentPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChange)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            shadowOf(getMainLooper()).idle()
            updatePasswordLiveData.postValue(
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