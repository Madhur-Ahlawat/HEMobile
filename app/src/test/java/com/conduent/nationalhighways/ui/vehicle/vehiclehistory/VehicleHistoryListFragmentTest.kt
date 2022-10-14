package com.conduent.nationalhighways.ui.vehicle.vehiclehistory

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.BaseActions
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
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class VehicleHistoryListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test get vehicles of the account to view history`() {
        every { viewModel.vehicleListVal } returns vehicleList

        launchFragmentInHiltContainer<VehicleHistoryListFragment> {
            onView(withId(R.id.vehicleHistoryTv)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleHistoryList)).check(matches(isDisplayed()))

            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234", "UK"),
                VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
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
            onView(withId(R.id.rvVehicleHistoryList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleHistoryList).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.rvVehicleHistoryList))
                .check(matches(BaseActions.atPosition(0, hasDescendant(withText("1234")))))
            onView(withId(R.id.rvVehicleHistoryList))
                .check(matches(BaseActions.atPosition(1, hasDescendant(withText("ABCD")))))

        }
    }

    @Test
    fun `test get vehicles of the account to view history for no vehicles`() {
        every { viewModel.vehicleListVal } returns vehicleList
        launchFragmentInHiltContainer<VehicleHistoryListFragment> {
            onView(withId(R.id.vehicleHistoryTv)).check(matches(isDisplayed()))

            val list = listOf<VehicleResponse>()
            vehicleList.postValue(Resource.Success(list))
            onView(withId(R.id.rvVehicleHistoryList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleHistoryList).adapter?.itemCount,
                null
            )

        }
    }

    @Test
    fun `test get vehicles of the account  to view history for unknown error`() {
        every { viewModel.vehicleListVal } returns MutableLiveData(Resource.DataError(""))

        launchFragmentInHiltContainer<VehicleHistoryListFragment> {
            onView(withId(R.id.vehicleHistoryTv)).check(matches(isDisplayed()))

            vehicleList.postValue(Resource.DataError("Unknown Error"))

            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }

        }
    }

    @Test
    fun `test click on vehicle item to view history`() {
        every { viewModel.vehicleListVal } returns vehicleList

        launchFragmentInHiltContainer<VehicleHistoryListFragment> {
            Navigation.setViewNavController(requireView(),navController)
            onView(withId(R.id.vehicleHistoryTv)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleHistoryList)).check(matches(isDisplayed()))

            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234", "UK"),
                VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
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
            onView(withId(R.id.rvVehicleHistoryList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleHistoryList).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.rvVehicleHistoryList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, BaseActions.clickOnViewChild(R.id.vrm_title)
                    )
                )

            verify(navController).navigate(R.id.action_vehicleHistoryListFragment_to_vehicleHistoryVehicleDetailsFragment)
        }
    }

}