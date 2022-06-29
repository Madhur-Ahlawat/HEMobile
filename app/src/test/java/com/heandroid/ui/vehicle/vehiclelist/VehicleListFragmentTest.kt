package com.heandroid.ui.vehicle.vehiclelist

import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
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
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class VehicleListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val navController: NavController = Mockito.mock(NavController::class.java)
    private val navCont = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test get vehicles of the account`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, false)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(
            bundle
        ) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))

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
    fun `test get vehicles of the account for no vehicles`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, false)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(
            bundle
        ) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))

            val list = listOf<VehicleResponse>()
            vehicleList.postValue(Resource.Success(list))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                null
            )

        }
    }

    @Test
    fun `test get vehicles of the account for unknown error`() {
        every { viewModel.vehicleListVal } returns MutableLiveData(Resource.DataError(""))
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, false)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(bundle) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
            vehicleList.postValue(Resource.DataError("Unknown Error"))

            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }

        }
    }

    @Test
    fun `test vehicle details by expanding vehicle item`() {
        every { viewModel.vehicleListVal } returns vehicleList
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, false)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(
            bundle
        ) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))

            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234", "UK"),
                VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
                false
            )
            val list = listOf(v1)
            vehicleList.postValue(Resource.Success(list))
            onView(withId(R.id.rvVehicleList)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleList).adapter?.itemCount,
                list.size
            )
            onView(withId(R.id.rvVehicleList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, clickOnViewChild(R.id.arrowImg)
                    )
                )
            onView(withId(R.id.rvVehicleList))
                .check(matches(atPosition(0, hasDescendant(withText("TATA")))))
            onView(withId(R.id.rvVehicleList))
                .check(matches(atPosition(0, hasDescendant(withText("Harrier")))))
            onView(withId(R.id.rvVehicleList))
                .check(matches(atPosition(0, hasDescendant(withText("black")))))
            onView(withId(R.id.rvVehicleList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, clickOnViewChild(R.id.arrowImg)
                    )
                )
            onView(withId(R.id.rvVehicleList))
                .check(matches(not(atPosition(0, hasDescendant(withText("black"))))))

        }
    }

    @Test
    fun `test add and remove button visibility for vehicle module`() {
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, false)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(
            bundle
        ) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
        }
    }

    // @Test
    fun `test add and remove button visibility for dashboard screen`() {
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.DATA, true)
        }
        launchFragmentInHiltContainer<VehicleListFragment>(
            bundle
        ) {
            onView(withId(R.id.yourVehicleTv)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).check(matches(not(isDisplayed())))
            onView(withId(R.id.removeVehicleBtn)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun `test add vehicle dialog, cancel button`() {
        launchFragmentInHiltContainer<VehicleListFragment> {
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).perform(BaseActions.forceClick())
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<Button>(R.id.cancel_btn)?.performClick()
                assert(dialogFragment.dialog?.isShowing == false)
            }
        }
    }

    @Test
    fun `test remove vehicle dialog, cancel button`() {
        launchFragmentInHiltContainer<VehicleListFragment> {
            onView(withId(R.id.removeVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicleBtn)).perform(BaseActions.forceClick())
            val dialogFragment =
                childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as RemoveVehicleDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.btnCancel)?.performClick()
            assert(dialogFragment.dialog?.isShowing == false)
        }
    }

    @Test
    fun `test add vehicle dialog, add button disabled for no vehicle number`() {
        launchFragmentInHiltContainer<VehicleListFragment> {
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).perform(BaseActions.forceClick())
            val dialogFragment =
                childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.isClickable?.let {
                assertFalse(
                    it
                )
            }
        }
    }

    //@Test
    fun `test add vehicle dialog, add button enabled for vehicle number`() {
        launchFragmentInHiltContainer<VehicleListFragment> {
            navCont.setGraph(R.navigation.navigation_vehicle_list)
            navCont.setCurrentDestination(R.id.chooseOptionFragment)
            Navigation.setViewNavController(requireView(), navCont)
            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.addVehicleBtn)).perform(BaseActions.forceClick())

            runTest {
                delay(1000)
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.add_vrm_input)).inRoot(isDialog())
                    .perform(typeTextIntoFocusedView("L062 1234"))
                    .perform(closeSoftKeyboard())
                delay(1000)
                val vehicle = VehicleResponse(
                    PlateInfoResponse(),
                    PlateInfoResponse("L062 1234", "UK"),
                    VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
                    false
                )
                (this@launchFragmentInHiltContainer as VehicleListFragment).onAddClick(vehicle)
                val bundle = Bundle().apply {
                    putParcelable(ConstantsTest.CREATE_ACCOUNT_DATA, null)
                    putParcelable(ConstantsTest.DATA, vehicle)
                }
                assertEquals(
                    navController.currentDestination?.id,
                    R.id.addVehicleDetailsFragment
                )
                val currentDestinationArgs = navCont.backStack.last().arguments
                Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bundle))
            }
        }
    }


}
