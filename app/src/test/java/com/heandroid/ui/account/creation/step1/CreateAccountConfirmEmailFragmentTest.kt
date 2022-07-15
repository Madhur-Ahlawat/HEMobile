package com.heandroid.ui.account.creation.step1

import android.os.Bundle
import android.os.Looper
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.BaseActions.forceClick
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@MediumTest
class CreateAccountConfirmEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountEmailViewModel>(relaxed = true)

    private val emailVerification = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    private val confirmEmailVerification = MutableLiveData<Resource<EmptyApiResponse?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel()
            )
        }
    }

    @Test
    fun `test confirm email otp screen visibility`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.otpView)).check(matches(isDisplayed()))
            onView(withId(R.id.etCodeLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvResend)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test resend email otp request for success`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            shadowOf(getMainLooper()).idle()
            runTest {
                onView(withId(R.id.tvResend)).check(matches(isDisplayed()))
                    .perform(forceClick())
                shadowOf(getMainLooper()).idle()
                emailVerification.value =
                    Resource.Success(
                        EmailVerificationResponse
                            ("200", "99890", "success", "12345")
                    )
                assertEquals("code sent successfully", ShadowToast.getTextOfLatestToast())
            }
        }
    }

    @Test
    fun `test resend email otp request for unknown error`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            shadowOf(getMainLooper()).idle()
            onView(withId(R.id.tvResend)).check(matches(isDisplayed()))
                    .perform(forceClick())
            runTest {
                shadowOf(getMainLooper()).idle()
                emailVerification.value = Resource.DataError("unknown error")
                shadowOf(getMainLooper()).idle()
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test confirm email otp for success`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.confirmEmailFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etCode)).check(matches(isDisplayed()))
                .perform(
                    ViewActions.clearText(),
                    ViewActions.typeText("123456")
                )

            runTest {
                delay(500)
                closeSoftKeyboard()
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
                    .perform(forceClick())
                confirmEmailVerification.value = Resource.Success(EmptyApiResponse(200, ""))
                assertEquals(
                    navController.currentDestination?.id,
                    R.id.accountTypeSelectionFragment
                )
            }
        }
    }

    @Test
    fun `test confirm email otp for success, navigate to next screen for edit mail`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.confirmEmailFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etCode)).check(matches(isDisplayed()))
                .perform(
                    ViewActions.clearText(),
                    ViewActions.typeText("123456")
                )

            runTest {
                delay(500)
                closeSoftKeyboard()
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
                    .perform(forceClick())
                confirmEmailVerification.value = Resource.Success(EmptyApiResponse(200, ""))
                assertEquals(
                    navController.currentDestination?.id,
                    R.id.paymentSummaryFragment
                )
            }
        }
    }

    @Test
    fun `test confirm email otp for error`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.etCode)).perform(ViewActions.clearText(),
                    BaseActions.forceTypeText("123456"))
                closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            shadowOf(getMainLooper()).idle()
            confirmEmailVerification.value = Resource.DataError("unknown error")
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }

        }
    }

}