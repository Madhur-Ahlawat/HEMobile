package com.conduent.nationalhighways.ui.checkpaidcrossings

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
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Resource
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
@LargeTest
class EnterVrmFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CheckPaidCrossingViewModel>(relaxed = true)

    private val vehicleInfoDetailsLiveData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.findVehicleLiveData } returns vehicleInfoDetailsLiveData
    }

    @Test
    fun `test enter vrm screen for success api call`() {
        launchFragmentInHiltContainer<EnterVrmFragment> {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.enterVrmFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.top_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.vrm_no)).check(matches(isDisplayed()))
            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.vrm_no)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            shadowOf(getMainLooper()).idle()
            vehicleInfoDetailsLiveData.postValue(
                Resource.Success(
                    VehicleInfoDetails(null)
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals(
                navController.currentDestination?.id,
                R.id.checkPaidCrossingChangeVrm
            )
        }
    }

    @Test
    fun `test enter vrm screen for error api call`() {
        launchFragmentInHiltContainer<EnterVrmFragment> {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.enterVrmFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.top_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.vrm_no)).check(matches(isDisplayed()))
            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.vrm_no)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("12345"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            shadowOf(getMainLooper()).idle()
            vehicleInfoDetailsLiveData.postValue(
                Resource.DataError(
                    "unknown error"
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals(
                navController.currentDestination?.id,
                R.id.checkPaidCrossingChangeVrm
            )
        }
    }

}