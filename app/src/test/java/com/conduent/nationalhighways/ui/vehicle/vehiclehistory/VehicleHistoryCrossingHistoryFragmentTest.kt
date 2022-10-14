package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

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
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryItem
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryResponse
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.vehicle.SelectedVehicleViewModel
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.ui.vehicle.crossinghistory.dialog.DownloadFormatSelectionFilterDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.data.DataFile
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
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@LargeTest
class VehicleHistoryCrossingHistoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    @BindValue
    @JvmField
    val selectedViewModel = mockk<SelectedVehicleViewModel>(relaxed = true)

    private val crossingList = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()

    private val selectedVehicleLiveData = MutableLiveData<VehicleResponse?>()
    private val selectedVehicle = VehicleResponse(
        PlateInfoResponse(),
        PlateInfoResponse("L062 1234", "UK"),
        VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
        false
    )

    private lateinit var navController: NavController

    @Before
    fun init() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)
    }

    @Test
    fun `test get crossing history of vehicle`() {
        every { viewModel.crossingHistoryVal } returns crossingList
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val list = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(list, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            selectedVehicleLiveData.postValue(selectedVehicle)
            crossingList.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvVehicleCrossingHistory)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleCrossingHistory).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.download_crossing_history_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.back_to_vehicle_list_btn)).check(matches(isDisplayed()))
            onView(withId(R.id.tvNoCrossing)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun `test get crossing history of vehicle for no crossings`() {
        every { viewModel.crossingHistoryVal } returns crossingList
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            val list = mutableListOf<CrossingHistoryItem?>()
            val crossingHistoryResponseData = CrossingHistoryResponse(list, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            runTest {
                selectedVehicleLiveData.postValue(selectedVehicle)
                delay(1000)
                crossingList.postValue(Resource.Success(crossingHistoryResponse))
                delay(500)
            }
            onView(withId(R.id.tvNoCrossing)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test get crossing history of vehicle for unknown error`() {
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData
        selectedVehicleLiveData.postValue(selectedVehicle)
        every { viewModel.crossingHistoryVal } returns MutableLiveData(Resource.DataError(""))
        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            runTest {
                delay(500)
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    //@Test
    fun `test download crossing history of vehicle`() {
        every { viewModel.crossingHistoryVal } returns crossingList
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val list = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(list, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            selectedVehicleLiveData.postValue(selectedVehicle)
            crossingList.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvVehicleCrossingHistory)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleCrossingHistory).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.download_crossing_history_btn)).perform(ViewActions.click())
            runTest {
                delay(500)
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as DownloadFormatSelectionFilterDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    //@Test
    fun `test endless scroll of crossing history of vehicle`() {
        every { viewModel.crossingHistoryVal } returns crossingList
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            val list = mutableListOf<CrossingHistoryItem?>()
            for (i  in 1 ..10) {
                DataFile.getCrossingHistoryItem("ABCD$i")?.let { list.add(it) }
            }
            val crossingHistoryResponseData = CrossingHistoryResponse(list, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            selectedVehicleLiveData.postValue(selectedVehicle)
            crossingList.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvVehicleCrossingHistory)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleCrossingHistory).adapter?.itemCount,
                list.size
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                onView(withId(R.id.rvVehicleCrossingHistory)).perform(ViewActions.swipeUp())
                    .perform(ViewActions.swipeUp())
                delay(2000)
            }
            every { viewModel.crossingHistoryVal } returns crossingList
            crossingList.postValue(Resource.Success(crossingHistoryResponse))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleCrossingHistory).adapter?.itemCount,
                list.size*2
            )
        }
    }

    @Test
    fun `test back to vehicles button of crossing history screen`() {
        every { viewModel.crossingHistoryVal } returns crossingList
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryCrossingHistoryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))

            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val list = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(list, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            selectedVehicleLiveData.postValue(selectedVehicle)
            crossingList.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvVehicleCrossingHistory)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleCrossingHistory).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.back_to_vehicle_list_btn)).perform(ViewActions.click())
            Mockito.verify(navController).popBackStack(R.id.vehicleHistoryListFragment, false)
        }
    }




}
