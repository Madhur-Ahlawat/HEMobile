package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
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
class UnUsedChargesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.CHECK_PAID_CHARGE_DATA_KEY, CheckPaidCrossingsResponse(
                    "", "", "", "",
                    "", "", "3", ""
                )
            )
            putParcelable(
                Constants.CHECK_PAID_REF_VRM_DATA_KEY, CheckPaidCrossingsOptionsModel(
                    "", "1234", false
                )
            )
        }

    }

    @Test
    fun `test un used charges screen with crossings`() {
        launchFragmentInHiltContainer<UnUsedChargesFragment>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.unUsedCharges)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
            onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvHistory).adapter?.itemCount,
                3
            )
            onView(withId(R.id.rvHistory))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, BaseActions.clickOnViewChild(R.id.change_vehicle)
                    )
                )
            assertEquals(
                navController.currentDestination?.id,
                R.id.enterVrmFragment
            )
        }
    }

    @Test
    fun `test un used charges screen with no crossings`() {
        bundle.apply {
            putParcelable(
                Constants.CHECK_PAID_CHARGE_DATA_KEY, CheckPaidCrossingsResponse(
                    "", "", "", "",
                    "", "", "0", ""
                )
            )
        }
        launchFragmentInHiltContainer<UnUsedChargesFragment>(bundle) {
            onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
            onView(withId(R.id.tvNoCrossing)).check(matches(isDisplayed()))
        }
    }

}