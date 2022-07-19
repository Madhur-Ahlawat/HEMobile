package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class CheckPaidCrossFragmentChangeVrmTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var bundle: Bundle
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putString(Constants.COUNTRY_TYPE, "UK")
            putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, "12345")
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
            putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, VehicleInfoDetails(null))
        }
    }

    @Test
    fun `test paid crossing for existed vehicle`() {
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.checkPaidCrossingChangeVrmConform
            )
        }
    }

    @Test
    fun `test paid crossing for non existed vehicle`() {
        bundle.apply {
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
        }
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.addVehicleDetailsFragment
            )
        }
    }
}