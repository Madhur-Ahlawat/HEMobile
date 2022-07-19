package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.utils.common.Constants
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
class CheckPaidCrossFragmentConformChangeVrmSuccessFullTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.CHECK_PAID_REF_VRM_DATA_KEY,
                CheckPaidCrossingsOptionsModel("", "", false)
            )
            putParcelable(
                Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS,
                VehicleInfoDetails(null)
            )
        }
    }

    @Test
    fun `test change vrm successful screen visibility`() {
        launchFragmentInHiltContainer<CheckPaidCrossFragmentConformChangeVrmSuccessFull>(bundle) {
            onView(withId(R.id.llRecommendParent)).check(matches(isDisplayed()))
            onView(withId(R.id.top_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.makeAnotherChange)).check(matches(isDisplayed()))
        }
    }
}