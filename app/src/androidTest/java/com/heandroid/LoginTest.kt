package com.heandroid

import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : BaseTest() {/*
    @Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    @Before
    fun setUp() {
        activityRule.launchActivity(Intent())
    }

    @Test
    fun loginSuccessful() {
        val loginPage = LoginPage()
        loginPage.goToLogin.perform(ViewActions.click())
        loginPage.login.check(ViewAssertions.matches(ViewMatchers.withText("Log In")))
        loginPage.userId.perform(ViewActions.typeText("johnsmith32"), ViewActions.closeSoftKeyboard())
        loginPage.passwordId.perform(ViewActions.typeText("Welcome1!"),
            ViewActions.closeSoftKeyboard())
        Espresso.closeSoftKeyboard()
        loginPage.login.perform(ViewActions.click())
        val basePage = BasePage()
        basePage.confirmNotificationPopup()
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(4000))
        val navigationPage = NavigationPage()
        navigationPage.homeTab.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val morePage = MorePage()
        morePage.goToToMenuAndLogout()
    }

    @Test
    fun loginValidationMessageDisplays() {
        val loginPage = LoginPage()
        loginPage.goToLogin.perform(ViewActions.click())
        loginPage.login.check(ViewAssertions.matches(IsNot.not<View>(ViewMatchers.isEnabled())))
        loginPage.userId.perform(ViewActions.typeText("1"), ViewActions.closeSoftKeyboard())
        loginPage.passwordId.perform(ViewActions.typeText("1"), ViewActions.closeSoftKeyboard())
        loginPage.login.perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(2000))
        Espresso.onView(withText("401"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        loginPage.login.check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        loginPage.userId.perform(ViewActions.typeText("123@!#"), ViewActions.closeSoftKeyboard())
        loginPage.passwordId.perform(ViewActions.typeText("testPassword"),
            ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(2000))
        Espresso.onView(withText("incorrect login"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        loginPage.login.check(ViewAssertions.matches(IsNot.not<View>(ViewMatchers.isEnabled())))
    }*/
}