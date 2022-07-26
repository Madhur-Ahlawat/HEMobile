package com.heandroid.ui.account.profile.pin

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
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.profile.ProfileDetailModel
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
class ProfilePinFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ProfileViewModel>(relaxed = true)

    private val updateProfileLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    private val updateAccountPinLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.updateProfileApiVal } returns updateProfileLiveData
        every { viewModel.updateAccountPinApiVal } returns updateAccountPinLiveData

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
    fun `test nominated profile screen update pin for success api call`() {
        launchFragmentInHiltContainer<ProfilePinFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.pinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            updateProfileLiveData.value = Resource.Success(EmptyApiResponse(200, ""))
            assertEquals(
                navController.currentDestination?.id,
                R.id.viewProfile
            )
        }
    }

    @Test
    fun `test nominated profile screen update pin for failed api call`() {
        launchFragmentInHiltContainer<ProfilePinFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.pinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            updateProfileLiveData.value = Resource.DataError("unknown error")
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test nominated profile screen change account pin for success api call`() {
        launchFragmentInHiltContainer<ProfilePinFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.pinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChangePin)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            updateAccountPinLiveData.value = Resource.Success(EmptyApiResponse(200, ""))
            assertEquals(
                navController.currentDestination?.id,
                R.id.updatePasswordSuccessfulFragment
            )
        }
    }

    @Test
    fun `test nominated profile screen change account pin for failed api call`() {
        launchFragmentInHiltContainer<ProfilePinFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.pinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnChangePin)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            updateAccountPinLiveData.value = Resource.DataError("unknown error")
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }
}