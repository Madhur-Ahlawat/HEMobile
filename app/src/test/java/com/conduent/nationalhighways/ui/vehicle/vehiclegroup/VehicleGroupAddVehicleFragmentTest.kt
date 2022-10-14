package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class VehicleGroupAddVehicleFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    @BindValue
    @JvmField
    val vehicleGroupViewModel = mockk<VehicleGroupMgmtViewModel>(relaxed = true)

    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test add vehicles screen visibility`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupAddVehicleFragment>(
            bundle
        ) {
            onView(withId(R.id.addVehicleToGroup)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))

            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234"),
                VehicleInfoResponse(),
                false
            )
            val v2 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("ABCD"),
                VehicleInfoResponse(),
                false
            )
            val list = listOf(v1, v2)
            vehicleList.postValue(Resource.Success(list))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )
        }
    }

    @Test
    fun `test add vehicles screen visibility for no vehicles`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupAddVehicleFragment>(
            bundle
        ) {
            onView(withId(R.id.addVehicleToGroup)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))

            val list = listOf<VehicleResponse>()
            vehicleList.postValue(Resource.Success(list))
            onView(withId(R.id.tvNoVehicles)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )
        }
    }

    @Test
    fun `test add vehicles screen visibility for unknown error`() {
        every { viewModel.vehicleListVal } returns vehicleList
        vehicleList.postValue(Resource.DataError("unknown error"))

        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupAddVehicleFragment>(
            bundle
        ) {
            vehicleList.postValue(Resource.DataError("unknown error"))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test add vehicles screen, cancel button`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupAddVehicleFragment>(
            bundle
        ) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.addVehicleToGroup)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.unAllocatedDesc2)).check(matches(isDisplayed()))

            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            Mockito.verify(navController).popBackStack()

        }
    }


}