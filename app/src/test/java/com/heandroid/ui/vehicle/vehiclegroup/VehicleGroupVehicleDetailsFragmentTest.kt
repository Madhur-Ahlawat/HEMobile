package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.os.Looper.getMainLooper
import android.widget.Button
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.BaseActions.atPosition
import com.heandroid.utils.BaseActions.clickOnViewChild
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.common.Resource
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
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
            putParcelable(ConstantsTest.DATA, VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234", "UK"),
                VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
                false
            ))
        }
        launchFragmentInHiltContainer<VehicleGroupVehicleDetailsFragment>(
            bundle
        ) {
            onView(withId(R.id.number)).check(matches(isDisplayed()))
            onView(withId(R.id.country)).check(matches(isDisplayed()))
            onView(withId(R.id.editDetailsBtn)).check(matches(isDisplayed()))
            onView(withText("1234")).check(matches(isDisplayed()))
            onView(withText("TATA")).check(matches(isDisplayed()))
            onView(withText("Harrier")).check(matches(isDisplayed()))

        }
    }
}