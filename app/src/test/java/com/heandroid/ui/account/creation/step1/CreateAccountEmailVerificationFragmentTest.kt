package com.heandroid.ui.account.creation.step1

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.data.model.createaccount.EmailVerificationResponse
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
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class CreateAccountEmailVerificationFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountEmailViewModel>(relaxed = true)

    private val emailVerification = MutableLiveData<Resource<EmailVerificationResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test sent email otp for success`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification

        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
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
//            Mockito.verify(navController)
//                .navigate(R.id.action_confirmEmailFragment_to_accountTypeSelectionFragment, bun)
        }
    }

    @Test
    fun `test sent email otp for failure`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification

        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
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
                        ("1", "99890", "success", "12345")
                )
        }
    }

    @Test
    fun `test sent email otp for unknown error`() {
        every { viewModel.emailVerificationApiVal } returns emailVerification

        launchFragmentInHiltContainer<CreateAccountEmailVerificationFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVerification)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvMsg)).check(matches(isDisplayed()))
            onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("test@test.com"))
            onView(withId(R.id.btn_action)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Shadows.shadowOf(Looper.getMainLooper()).idle()
            emailVerification.postValue(Resource.DataError("unknown error"))
        }
    }
}