package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
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
import com.heandroid.data.model.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.data.model.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
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
import kotlinx.coroutines.test.runBlockingTest
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
class OTPForgotPasswordTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var bundle: Bundle

    @BindValue
    @JvmField
    val viewModel = mockk<ForgotPasswordViewModel>(relaxed = true)

    private val otpLiveData = MutableLiveData<Resource<SecurityCodeResponseModel?>?>()

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                "data",
                RequestOTPModel(
                    optionType = Constants.SMS, ""
                )
            )
        }
    }

    @Test
    fun `test otp forgot password screen visibility`() {
        launchFragmentInHiltContainer<OTPForgotPassword>(bundle) {
            onView(withId(R.id.top_title)).check(matches(isDisplayed()))
            onView(withId(R.id.sub_title)).check(matches(isDisplayed()))
            onView(withId(R.id.edt_otp)).check(matches(isDisplayed()))
            onView(withId(R.id.not_received_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.resend_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_verify)).check(matches(isDisplayed()))
        }
    }

    //@Test
    fun `test otp forgot password screen, otp for success api call for sms`() {
        every { viewModel.otp } returns otpLiveData
        launchFragmentInHiltContainer<OTPForgotPassword>(bundle) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.otpFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.top_title)).check(matches(isDisplayed()))
            otpLiveData.postValue(
                Resource.Success(
                    SecurityCodeResponseModel(
                        "", 10000L, "", false
                    )
                )
            )
            runTest {
                onView(withId(R.id.edt_otp)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("123456"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_verify)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createPasswordFragment
            )
        }
    }

    //@Test
    fun `test otp forgot password screen, otp for success api call for email for timeout`() {
        val bund = Bundle().apply {
            putParcelable(
                "data",
                RequestOTPModel(
                    optionType = Constants.EMAIL, ""
                )
            )
        }
        every { viewModel.otp } returns otpLiveData
        launchFragmentInHiltContainer<OTPForgotPassword>(bund) {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.otpFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.top_title)).check(matches(isDisplayed()))
            otpLiveData.postValue(
                Resource.Success(
                    SecurityCodeResponseModel(
                        "", 1L, "", false
                    )
                )
            )
            runTest {
                onView(withId(R.id.edt_otp)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("123456"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_verify)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }
}