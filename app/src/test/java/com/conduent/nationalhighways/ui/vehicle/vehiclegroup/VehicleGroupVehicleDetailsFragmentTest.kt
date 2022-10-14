package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
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
class VehicleGroupVehicleDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test vehicle details screen visibility`() {
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleResponse(
                    PlateInfoResponse(),
                    PlateInfoResponse("1234", "UK"),
                    VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
                    false
                )
            )
            putParcelable(
                Constants.VEHICLE_GROUP,
                VehicleGroupResponse("", "", "")
            )
        }
        launchFragmentInHiltContainer<VehicleGroupVehicleDetailsFragment>(
            bundle
        ) {
            onView(withId(R.id.number)).check(matches(isDisplayed()))
            onView(withId(R.id.country)).check(matches(isDisplayed()))
            onView(withId(R.id.editDetailsBtn)).check(matches(isDisplayed()))
        }
    }
}