package com.heandroid.ui.vehicle.vehiclegroup

import android.os.Looper.getMainLooper
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleGroupMngmtResponse
import com.heandroid.data.model.vehicle.VehicleGroupResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Resource
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
class VehicleGroupMngmtFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleGroupMgmtViewModel>(relaxed = true)

    private val vehicleGroupList = MutableLiveData<Resource<List<VehicleGroupResponse?>?>?>()
    private val vehicleGroupDeleteLiveData =
        MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test vehicle group management screen visibility`() {
        val v1 = VehicleGroupResponse("", "", "")
        val v2 = VehicleGroupResponse("", "", "")
        val list = listOf(v1, v2)
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleGroupList)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size +1
            )
        }
    }


    @Test
    fun `test vehicle group management screen visibility for no groups`() {
        val list = listOf<VehicleGroupResponse>()
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size +1
            )
        }
    }

    @Test
    fun `test vehicle group screen for unknown error`() {
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        vehicleGroupList.postValue(Resource.DataError("Unknown error"))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            shadowOf(getMainLooper()).idle()
            vehicleGroupList.postValue(Resource.DataError("Unknown error"))
            runBlockingTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test create new vehicle group screen navigation`() {
        val list = listOf<VehicleGroupResponse>()
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size +1
            )
            onView(withId(R.id.createVehicleGroupBtn)).perform(ViewActions.click())
//            Mockito.verify(navController)
//                .navigate(R.id.action_vehicleGroupMngmtFragment_to_createAndRenameVehicleGroupFragment, bun)
        }
    }

    //@Test
    fun `test delete vehicle group for success`() {
        val v1 = VehicleGroupResponse("123", "Group1", "10")
        val v2 = VehicleGroupResponse("456", "Group2", "30")
        val list = listOf(v1, v2)
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        every { viewModel.deleteVehicleGroupApiVal } returns vehicleGroupDeleteLiveData
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleGroupList)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size +1
            )
            onView(withId(R.id.rvVehicleGroupList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1, BaseActions.clickOnViewChild(R.id.cbVehicleGroup)
                    )
                )

            shadowOf(getMainLooper()).idle()
            onView(withId(R.id.deleteVehicleGroupBtn)).perform(ViewActions.click())
            every { viewModel.deleteVehicleGroupApiVal } returns vehicleGroupDeleteLiveData
            runBlockingTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag("") as DeleteVehicleGroupDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<Button>(R.id.yesBtn)?.performClick()
                assert(dialogFragment.dialog?.isShowing == false)
            }
            every { viewModel.deleteVehicleGroupApiVal } returns vehicleGroupDeleteLiveData
            shadowOf(getMainLooper()).idle()
            vehicleGroupDeleteLiveData.postValue(
                Resource.Success(
                    VehicleGroupMngmtResponse(
                        true,
                        "",
                        ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals("vehicle Group deleted successfully", ShadowToast.getTextOfLatestToast())
        }
    }

    @Test
    fun `test delete vehicle group for unknown error`() {
        val v1 = VehicleGroupResponse("123", "Group1", "10")
        val v2 = VehicleGroupResponse("456", "Group2", "30")
        val list = listOf(v1, v2)
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        every { viewModel.deleteVehicleGroupApiVal } returns vehicleGroupDeleteLiveData
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleGroupList)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            shadowOf(getMainLooper()).idle()
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size+1
            )
            onView(withId(R.id.rvVehicleGroupList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1, BaseActions.clickOnViewChild(R.id.cbVehicleGroup)
                    )
                )

            onView(withId(R.id.deleteVehicleGroupBtn)).perform(ViewActions.click())
            runBlockingTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag("") as DeleteVehicleGroupDialog
                assert(dialogFragment.dialog?.isShowing == true)
                dialogFragment.dialog?.findViewById<Button>(R.id.yesBtn)?.performClick()
                assert(dialogFragment.dialog?.isShowing == false)
            }

            every { viewModel.deleteVehicleGroupApiVal } returns vehicleGroupDeleteLiveData
            vehicleGroupDeleteLiveData.postValue(Resource.DataError("Unknown error"))
            shadowOf(getMainLooper()).idle()
            runBlockingTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test rename vehicle group for next screen navigation`() {
        val v1 = VehicleGroupResponse("123", "Group1", "10")
        val v2 = VehicleGroupResponse("456", "Group2", "30")
        val list = listOf(v1, v2)
        every { viewModel.getVehicleGroupListApiVal } returns vehicleGroupList
        vehicleGroupList.postValue(Resource.Success(list))
        launchFragmentInHiltContainer<VehicleGroupMngmtFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvGroupDesc1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGroupDesc3)).check(matches(isDisplayed()))
            onView(withId(R.id.rvVehicleGroupList)).check(matches(isDisplayed()))
            onView(withId(R.id.createVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.deleteVehicleGroupBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.renameVehicleGroupBtn)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvVehicleGroupList).adapter?.itemCount,
                list.size+1
            )
            onView(withId(R.id.rvVehicleGroupList))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1, BaseActions.clickOnViewChild(R.id.cbVehicleGroup)
                    )
                )

            onView(withId(R.id.renameVehicleGroupBtn)).perform(ViewActions.click())
//            Mockito.verify(navController)
//                .navigate(
//                    R.id.action_vehicleGroupMngmtFragment_to_createAndRenameVehicleGroupFragment,
//                    bun
//                )
        }
    }


}