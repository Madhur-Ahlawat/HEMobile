package com.heandroid.auth.login

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.auth.launchFragmentInHiltContainer
import com.heandroid.ui.auth.login.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(requireView(),navController)
        }
        onView(withId(R.id.edt_email)).perform(click())
        verify(navController).navigate(R.id.action_loginFragment_to_forgotEmailFragment)
    }


    @Test
    fun clickToForgotPasswordFragment(){
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(requireView(),navController)
        }
        onView(withId(R.id.edt_email)).perform(click())

        verify(navController).navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }
}