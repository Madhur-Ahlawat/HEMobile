package com.heandroid.ui.account.creation.step1

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
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.data.DataFile
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
@MediumTest
class CreateAccountEmailVerificationFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountEmailViewModel>(relaxed = true)

    private val emailVerification = MutableLiveData<Resource<EmailVerificationResponse?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test sent email otp for success`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.emailVerification)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvMsg)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("test@test.com"))
            onView(withId(R.id.btn_action)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            emailVerification.value =
                Resource.Success(
                    EmailVerificationResponse
                        ("0", "99890", "success", "12345")
                )
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.confirmEmailFragment
            )
        }
    }

    @Test
    fun `test sent email otp for success, for edit email`() {
        val bundle = Bundle().apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL_KEY
            )
            putParcelable(
                Constants.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel()
            )
        }
        every { viewModel.emailVerificationApiVal } returns emailVerification
        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.emailVerification)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvMsg)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("test@test.com"))
            onView(withId(R.id.btn_action)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            emailVerification.value =
                Resource.Success(
                    EmailVerificationResponse
                        ("0", "99890", "success", "12345")
                )
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.confirmEmailFragment
            )
        }
    }

    @Test
    fun `test sent email otp for failure`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification

        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.emailVerification)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvMsg)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_action)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            emailVerification.value =
                Resource.Success(
                    EmailVerificationResponse
                        ("1", "99890", "success", "12345")
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
    fun `test sent email otp for unknown error`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvMsg)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_action)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            shadowOf(getMainLooper()).idle()
            emailVerification.postValue(Resource.DataError("unknown error"))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }
}