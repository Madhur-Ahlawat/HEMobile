package com.conduent.nationalhighways.ui.auth.login

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
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.auth.login.LoginResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<LoginViewModel>(relaxed = true)

    private val loginLiveData = MutableLiveData<Resource<LoginResponse?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test login screen visibility`() {
        every { viewModel.login } returns loginLiveData
        launchFragmentInHiltContainer<LoginActivity> {
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_pwd)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_username)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test login screen, navigate to forgot email screen`() {
        every { viewModel.login } returns loginLiveData
        launchFragmentInHiltContainer<LoginActivity> {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.loginFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_pwd)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_username)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.forgotEmailFragment
            )
        }
    }

    @Test
    fun `test login screen, navigate to forgot password screen`() {
        every { viewModel.login } returns loginLiveData
        launchFragmentInHiltContainer<LoginActivity> {
            navController.setGraph(R.navigation.navigation_auth)
            navController.setCurrentDestination(R.id.loginFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_pwd)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_username)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.forgotPasswordFragment
            )

        }
    }

    @Test
    fun `test login screen, login api for success`() {
        every { viewModel.login } returns loginLiveData
        launchFragmentInHiltContainer<LoginActivity> {
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_pwd)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_username)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("100313904"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_pwd)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Welcome1"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            loginLiveData.value = Resource.Success(DataFile.getLoginResponse())

        }
    }

    @Test
    fun `test login screen, login api for unknown error`() {
        every { viewModel.login } returns loginLiveData
        launchFragmentInHiltContainer<LoginActivity> {
            onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
            onView(withId(R.id.tf_pwd)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_username)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("100313904"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.edt_pwd)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Welcome1"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            loginLiveData.postValue(Resource.DataError("unknown error"))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }

        }
    }
}