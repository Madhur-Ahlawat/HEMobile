package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountResponseModel
import com.conduent.nationalhighways.ui.account.creation.step6.CreateAccountSuccessfulFragment
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
@MediumTest
class CreateAccountSuccessfulFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                "response",
                CreateAccountResponseModel(
                    "", "", "",
                    "", ""
                )
            )
        }
    }

    @Test
    fun `test payment successful screen visibility`() {
        launchFragmentInHiltContainer<CreateAccountSuccessfulFragment>(bundle) {
            onView(withId(R.id.appCompatTextView)).check(matches(isDisplayed()))
            onView(withId(R.id.clAccountNo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvAccountNo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownloaReceipt)).check(matches(isDisplayed()))
            onView(withId(R.id.btnClose)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test payment successful screen, navigate to next screen`() {
        launchFragmentInHiltContainer<CreateAccountSuccessfulFragment>(bundle) {
            onView(withId(R.id.appCompatTextView)).check(matches(isDisplayed()))
            onView(withId(R.id.clAccountNo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvAccountNo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownloaReceipt)).check(matches(isDisplayed()))
            onView(withId(R.id.btnClose)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }
}