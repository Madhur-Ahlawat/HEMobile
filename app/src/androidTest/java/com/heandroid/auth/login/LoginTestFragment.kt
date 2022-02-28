package com.heandroid.auth.login

import com.heandroid.auth.launchFragmentInHiltContainer
import com.heandroid.ui.auth.login.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class LoginTestFragment {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun loadFragment() {
        val fragment = launchFragmentInHiltContainer<LoginFragment> {

        }
    }





}