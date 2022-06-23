package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class AddVehicleDoneFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountVehicleViewModel>(relaxed = true)

    private lateinit var navController: NavController
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)
        val vehicle = VehicleResponse(
            PlateInfoResponse("L062 1234", "Non-UK"),
            PlateInfoResponse("L062 1234", "Non-UK"),
            VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
            false
        )
        bundle = Bundle().apply {
            putParcelable(ConstantsTest.DATA,vehicle)
        }
    }

    @Test
    fun `test add vehicle done screen visibility for vehicle add flow`() {
        bundle.apply {
            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, ConstantsTest.VEHICLE_SCREEN_TYPE_ADD)
        }
        launchFragmentInHiltContainer<AddVehicleDoneFragment>(bundle) {
            onView(withId(R.id.tickLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("TATA")))))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("Harrier")))))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("black")))))
            onView(withId(R.id.conform_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test add vehicle done screen visibility for vehicle add flow, test back button`() {
        bundle.apply {
            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, ConstantsTest.VEHICLE_SCREEN_TYPE_ADD)
        }
        launchFragmentInHiltContainer<AddVehicleDoneFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tickLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.conform_btn)).check(matches(isDisplayed()))
                .perform(click())
//            Mockito.verify(navController).navigate(R.id.action_addVehicleDoneFragment_to_vehicleListFragment, bun)
        }
    }

    @Test
    fun `test add vehicle done screen visibility for payment flow`() {
        bundle.apply {
            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, ConstantsTest.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT)
        }
        launchFragmentInHiltContainer<AddVehicleDoneFragment>(bundle) {
            onView(withId(R.id.tvYourVehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("TATA")))))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("Harrier")))))
            onView(withId(R.id.recycler_view_header))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("black")))))
            onView(withId(R.id.conform_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test add vehicle done screen visibility for payment flow, test back button`() {
        bundle.apply {
            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, ConstantsTest.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT)
        }
        launchFragmentInHiltContainer<AddVehicleDoneFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvYourVehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.conform_btn)).check(matches(isDisplayed()))
                .perform(click())
//            Mockito.verify(navController).navigate(R.id.action_addVehicleDoneFragment_to_makeOneOffPaymentCrossingFragment)
        }
    }

}