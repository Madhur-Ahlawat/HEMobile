package com.heandroid.ui.account.creation.step1

import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions.forceClick
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
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class CreateAccountConfirmEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountEmailViewModel>(relaxed = true)

    private val emailVerification = MutableLiveData<Resource<EmailVerificationResponse?>?>()
    private val confirmEmailVerification = MutableLiveData<Resource<EmptyApiResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

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
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            runBlockingTest {
                onView(withId(R.id.tvResend)).check(matches(isDisplayed()))
                    .perform(forceClick())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
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
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            runBlockingTest {
                onView(withId(R.id.tvResend)).check(matches(isDisplayed()))
                    .perform(forceClick())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                emailVerification.value = Resource.DataError("unknown error")
            }
        }
    }

    @Test
    fun `test confirm email otp for success`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etCode)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("test@test.com"))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val emptyApiResponse = Mockito.mock(EmptyApiResponse::class.java)
            confirmEmailVerification.value = Resource.Success(emptyApiResponse)
//            Mockito.verify(navController)
//                .navigate(R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment, bun)
        }
    }

    @Test
    fun `test confirm email otp for error`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification
        every { viewModel.confirmEmailApiVal } returns confirmEmailVerification

        launchFragmentInHiltContainer<CreateAccountConfirmEmailFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etCode)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("test@test.com"))
            runBlockingTest {
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                confirmEmailVerification.value = Resource.DataError("unknown error")
            }
        }
    }

}