package com.heandroid.hiltTesting.login

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.ui.auth.forgot.email.ForgotEmailFragment
import com.heandroid.ui.auth.forgot.password.ForgotPasswordFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.heandroid.hiltTesting.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class LoginTestFragment {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }


    @Test
    fun isEmailNotEmpty(){
        onView(withId(R.id.edt_email)).check(isNotNull())
    }

    @Test
    fun isPasswordNotEmpty(){
        onView(withId(R.id.edt_pwd)).check(isNotNull())
    }

    @Test
    fun clickToForgotEmailFragment(){
         // val navController = mock(NavController::class.java)
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ForgotEmailFragment> {
            navController.setGraph(R.navigation.navigation_auth)
            Navigation.setViewNavController(requireView(),navController)
        }
        onView(withId(R.id.tv_forgot_username)).perform(click())
        verify(navController).navigate(R.id.action_loginFragment_to_forgotEmailFragment)
        //action_loginFragment_to_forgotEmailFragment


//        // Verify that performing a click changes the NavController’s state
//        onView(ViewMatchers.withId(R.id.play_btn)).perform(ViewActions.click())
//        assertThat(navController.currentDestination?.id).isEqualTo(R.id.in_game)
    }


    @Test
    fun clickToForgotPasswordFragment(){
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ForgotPasswordFragment> {
            Navigation.setViewNavController(requireView(),navController)
        }
        onView(withId(R.id.tv_forgot_username)).perform(click())

        verify(navController).navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }
}