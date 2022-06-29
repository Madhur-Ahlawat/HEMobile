package com.heandroid.ui.account.creation.step1

import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.utils.BaseActions
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
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