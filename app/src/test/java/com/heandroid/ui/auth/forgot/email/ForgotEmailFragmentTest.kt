package com.heandroid.ui.auth.forgot.email

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
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
@MediumTest
class ForgotEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ForgotEmailViewModel>(relaxed = true)

    private val forgotEmailLiveData = MutableLiveData<Resource<ForgotEmailResponseModel?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test forgot email screen visibility`() {
        every { viewModel.forgotEmail } returns forgotEmailLiveData
        launchFragmentInHiltContainer<ForgotEmailFragment> {
            onView(withId(R.id.ll_enter_details)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_account_number)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_post_code)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test forgot email screen, get forgotten email`() {
        every { viewModel.forgotEmail } returns forgotEmailLiveData
        every {
            viewModel.loadUserName("user name")
        } returns StringBuffer("us*****me")
        launchFragmentInHiltContainer<ForgotEmailFragment> {
            onView(withId(R.id.ll_enter_details)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_account_number)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_post_code)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_account_number)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("10031475"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_post_code)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("cm151aa"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Shadows.shadowOf(Looper.getMainLooper()).idle()
            forgotEmailLiveData.postValue(
                Resource.Success(
                    ForgotEmailResponseModel(
                        "user name"
                    )
                )
            )
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            onView(withId(R.id.ll_username)).check(matches(isDisplayed()))
            onView(withText("us*****me")).check(matches(isDisplayed()))
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test forgot email screen, for unknown error`() {
        every { viewModel.forgotEmail } returns forgotEmailLiveData
        every {
            viewModel.loadUserName("user name")
        } returns StringBuffer("us*****me")
        launchFragmentInHiltContainer<ForgotEmailFragment> {
            onView(withId(R.id.ll_enter_details)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_account_number)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_post_code)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_account_number)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("10031475"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_post_code)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("cm151aa"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Shadows.shadowOf(Looper.getMainLooper()).idle()
            forgotEmailLiveData.postValue(
                Resource.DataError(
                   "unknown error"
                )
            )
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

}