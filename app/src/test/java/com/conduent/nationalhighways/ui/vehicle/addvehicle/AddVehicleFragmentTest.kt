package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.AddVehicleDialog
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.AddVehicleVRMDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class AddVehicleFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @BindValue
//    @JvmField
//    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    private lateinit var navController: NavController

    @Before
    fun init() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)
    }

    @Test
    fun `test add vehicle screen view in portrait`() {
        launchFragmentInHiltContainer<AddVehicleFragment> {
            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
        }
    }


    @Test
    fun `test add vehicle dialog of add vehicle screen, cancel button`() {
        launchFragmentInHiltContainer<AddVehicleFragment> {
            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
                .perform(click())

            runTest {
                delay(1000)
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleVRMDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.isClickable?.let {
                    Assert.assertFalse(
                        it
                    )
                }
                dialogFragment.dialog?.findViewById<Button>(R.id.cancel_btn)?.performClick()
                assert(dialogFragment.dialog?.isShowing == false)
            }
        }
    }

    @Test
    fun `test add vehicle dialog of add vehicle screen,add button`() {
        launchFragmentInHiltContainer<AddVehicleFragment> {
            Navigation.setViewNavController(this.requireView(), navController)

            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
                .perform(click())

            runTest {
                delay(1000)
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleVRMDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.isClickable?.let {
                    Assert.assertFalse(
                        it
                    )
                }
                val vehicle = VehicleResponse(
                    PlateInfoResponse(),
                    PlateInfoResponse("L062 1234", "UK"),
                    VehicleInfoResponse(),
                    false
                )
                (this@launchFragmentInHiltContainer as AddVehicleFragment).onAddClick(vehicle)
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, vehicle)
                }
//                Mockito.verify(navController).navigate(R.id.addVehicleDetailsFragment, bundle)
            }
        }
    }
}