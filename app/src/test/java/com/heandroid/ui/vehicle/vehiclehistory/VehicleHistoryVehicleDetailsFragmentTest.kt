package com.heandroid.ui.vehicle.vehiclehistory

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.vehicle.SelectedVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.BaseActions.forceClick
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
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowInstrumentation.getInstrumentation

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class VehicleHistoryVehicleDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    @BindValue
    @JvmField
    val selectedViewModel = mockk<SelectedVehicleViewModel>(relaxed = true)


    private val selectedVehicleLiveData = MutableLiveData<VehicleResponse?>()
    private val updateVehicleLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    private val selectedVehicle = VehicleResponse(
        PlateInfoResponse(),
        PlateInfoResponse("L062 1234", "UK", vehicleComments = "new comment"),
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
    fun `test vehicle details of vehicle screen`() {
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryVehicleDetailsFragment> {
            onView(withId(R.id.saveBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.backToVehiclesBtn)).check(matches(isDisplayed()))

            selectedVehicleLiveData.postValue(selectedVehicle)

            onView(withText("Registration number")).check(matches(isDisplayed()))
            onView(withText("Country marker")).check(matches(isDisplayed()))
            onView(withText("L062 1234")).check(matches(isDisplayed()))
            onView(withText("UK")).check(matches(isDisplayed()))
            onView(withText("Harrier")).check(matches(isDisplayed()))
        }
    }


    @Test
    fun `test back to vehicles list button`() {
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryVehicleDetailsFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.saveBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.backToVehiclesBtn)).check(matches(isDisplayed()))

            selectedVehicleLiveData.postValue(selectedVehicle)

            onView(withText("Registration number")).check(matches(isDisplayed()))
            onView(withText("Country marker")).check(matches(isDisplayed()))
            onView(withText("L062 1234")).check(matches(isDisplayed()))
            onView(withText("UK")).check(matches(isDisplayed()))
            onView(withId(R.id.backToVehiclesBtn)).perform(ViewActions.click())
            Mockito.verify(navController).popBackStack(R.id.vehicleHistoryListFragment, false)
        }
    }

    @Test
    fun `test update vehicle comments of vehicle`() {
        every { selectedViewModel.selectedVehicleResponse } returns selectedVehicleLiveData
        every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData

        launchFragmentInHiltContainer<VehicleHistoryVehicleDetailsFragment> {
            onView(withId(R.id.backToVehiclesBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.saveBtn)).check(matches(not(isClickable())))

            selectedVehicleLiveData.postValue(selectedVehicle)

            onView(withId(R.id.edt_note))
                    .check(matches(withText("new comment")))

            val updatedVehicle = selectedVehicle
            updatedVehicle.plateInfo?.vehicleComments = "updated comment"
            val emptyApiResponse = Mockito.mock(EmptyApiResponse::class.java)
            runBlockingTest {
                delay(1000)
                updateVehicleLiveData.postValue(Resource.Success(emptyApiResponse))
//                delay(1000)
//                every { selectedViewModel.selectedVehicleResponse } returns MutableLiveData<VehicleResponse?>(updatedVehicle)
//                selectedVehicleLiveData.postValue(updatedVehicle)
//                onView(withId(R.id.edt_note))
//                    .check(matches(withText("new comment")))
            }
        }
    }

    @Test
    fun `test update vehicle comments of vehicle for unknown error`() {
        runBlockingTest {
            every { viewModel.updateVehicleApiVal } returns updateVehicleLiveData
            updateVehicleLiveData.postValue(Resource.DataError("Unknown error"))
            launchFragmentInHiltContainer<VehicleHistoryVehicleDetailsFragment> {
                shadowOf(getMainLooper()).idle()
                runBlockingTest {
                    delay(1000)
                    val dialogFragment =
                        requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                    assert(dialogFragment.dialog?.isShowing == true)
                }
            }
        }
    }


}