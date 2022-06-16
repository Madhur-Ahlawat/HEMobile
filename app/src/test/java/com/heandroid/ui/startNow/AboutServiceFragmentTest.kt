package com.heandroid.ui.startNow

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.heandroid.R
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AboutServiceFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test about screen visibility`() {
        launchFragmentInHiltContainer<AboutServiceFragment> {
            Espresso.onView(ViewMatchers.withId(R.id.tv_use_this_service))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.tv_link))
        }
    }




}