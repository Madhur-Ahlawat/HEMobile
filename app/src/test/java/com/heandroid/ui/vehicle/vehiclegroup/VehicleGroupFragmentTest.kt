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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.BaseActions
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
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class VehicleGroupFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    @BindValue
    @JvmField
    val vehicleGroupViewModel = mockk<VehicleGroupMgmtViewModel>(relaxed = true)

    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val searchVehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val unallocatedVehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val updateVehicleLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    private val searchVehicleLiveData = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val navController: NavController = Mockito.mock(NavController::class.java)
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        every { vehicleGroupViewModel.vehicleListVal } returns vehicleList
        every { vehicleGroupViewModel.searchVehicleVal } returns searchVehicleList
        every { viewModel.removeVehiclesFromGroupApiVal } returns updateVehicleLiveData
        every { viewModel.vehicleListVal } returns unallocatedVehicleList
    }

    @Test
    fun `test vehicle group screen visibility`() {
        launchFragmentInHiltContainer<VehicleGroupFragment>(bundle) {
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

            onView(withId(R.id.groupName)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))

            runTest {
                delay(2000)
                shadowOf(getMainLooper()).idle()
                vehicleList.postValue(Resource.Success(list))
                vehicleList.postValue(Resource.Success(list))
                assertEquals(
                    requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                    0
                )
            }

        }
    }

    @Test
    fun `test vehicle group screen visibility for no vehicles`() {
        launchFragmentInHiltContainer<VehicleGroupFragment>(
            bundle
        ) {
            val list = listOf<VehicleResponse>()

            onView(withId(R.id.groupName)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            vehicleList.postValue(Resource.Success(list))
            shadowOf(getMainLooper()).idle()
            onView(withId(R.id.tvNoVehicles)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleList)).check(matches(not(isDisplayed())))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )
        }
    }

    @Test
    fun `test vehicle group screen visibility for unknown error`() {
        vehicleList.postValue(Resource.DataError("unknown error"))
        launchFragmentInHiltContainer<VehicleGroupFragment>(bundle) {
            shadowOf(getMainLooper()).idle()
            vehicleList.postValue(Resource.DataError("unknown error"))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    //@Test
    fun `test remove vehicles from vehicle group for success`() {
        val emptyApiResponse = Mockito.mock(EmptyApiResponse::class.java)
        every { vehicleGroupViewModel.vehicleListVal } returns vehicleList
        every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupFragment>(bundle) {
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

            onView(withId(R.id.groupName)).check(matches(isDisplayed()))
                .check(matches(withText("Test Group")))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )

            onView(withId(R.id.rvVehicleList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, BaseActions.clickOnViewChild(R.id.cbVehicleGroup)
                    )
                )
            shadowOf(getMainLooper()).idle()
            every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData

            runTest {
                every { viewModel.updateVehicleApiVal } returns MutableLiveData(
                    Resource.Success(
                        emptyApiResponse
                    )
                )
                delay(500)
                onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
                    .perform(click())
                delay(500)
                updateVehicleLiveData.postValue(Resource.Success(emptyApiResponse))
                delay(500)
                assertEquals("vehicle removed successfully", ShadowToast.getTextOfLatestToast())
            }
        }
    }

    //@Test
    fun `test remove vehicles from vehicle group for unknown error`() {
        every { vehicleGroupViewModel.vehicleListVal } returns vehicleList
        every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData
        val bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.DATA, VehicleGroupResponse(
                    "1234", "Test Group", "10"
                )
            )
        }
        launchFragmentInHiltContainer<VehicleGroupFragment>(
            bundle
        ) {
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

            onView(withId(R.id.groupName)).check(matches(isDisplayed()))
                .check(matches(withText("Test Group")))
            onView(withId(R.id.bulkUploadBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )

            onView(withId(R.id.rvVehicleList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, BaseActions.clickOnViewChild(R.id.cbVehicleGroup)
                    )
                )
            every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
                .perform(click())
            shadowOf(getMainLooper()).idle()
            every { viewModel.updateVehicleApiVal } returns MutableLiveData(Resource.DataError("Unknown error"))

            updateVehicleLiveData.postValue(Resource.DataError("Unknown error"))
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

}