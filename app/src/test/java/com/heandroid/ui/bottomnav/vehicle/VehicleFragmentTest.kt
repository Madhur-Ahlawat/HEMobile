package com.heandroid.ui.bottomnav.vehicle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
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
class VehicleFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @BindValue
//    @JvmField
//    val session = mockk<SessionManager>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test vehicle screen for standard account flow`() {
        launchFragmentInHiltContainer<VehicleFragment> {
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_management_lyt)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun `test vehicle screen for standard account, vehicle list click`() {
        launchFragmentInHiltContainer<VehicleFragment> {
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_management_lyt)).check(matches(not(isDisplayed())))
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test vehicle screen for standard account, add vehicle click`() {
        launchFragmentInHiltContainer<VehicleFragment> {
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_management_lyt)).check(matches(not(isDisplayed())))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test vehicle screen for standard account, crossing history click`() {
        launchFragmentInHiltContainer<VehicleFragment> {
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_management_lyt)).check(matches(not(isDisplayed())))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

    @Test
    fun `test vehicle screen for standard account, vehicle history click`() {
        launchFragmentInHiltContainer<VehicleFragment> {
            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.add_vehicle_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicle_management_lyt)).check(matches(not(isDisplayed())))
            onView(withId(R.id.vehicle_history_lyt)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        }
    }

//    @Test
//    fun `test vehicle screen for business account flow`() {
//        every { session.fetchAccountType() } returns ConstantsTest.BUSINESS_ACCOUNT
//        launchFragmentInHiltContainer<VehicleFragment> {
//            onView(withId(R.id.vehicle_list_lyt)).check(matches(isDisplayed()))
//            onView(withId(R.id.add_vehicle_lyt)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.vehicle_history_lyt)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.vehicle_crossing_history_lyt)).check(matches(isDisplayed()))
//            onView(withId(R.id.vehicle_management_lyt)).check(matches(isDisplayed()))
//        }
//    }

}