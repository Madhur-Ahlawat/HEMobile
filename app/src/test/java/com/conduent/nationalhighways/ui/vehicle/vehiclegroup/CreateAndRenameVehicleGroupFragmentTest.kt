package com.conduent.nationalhighways.ui.vehicle.vehiclegroup

import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupMngmtResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleGroupResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
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
import org.robolectric.shadows.ShadowToast

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class CreateAndRenameVehicleGroupFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleGroupMgmtViewModel>(relaxed = true)

    private val renameVehicleGroupLiveData =
        MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()
    private val createVehicleGroupLiveData =
        MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test for create new group visibility`() {
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, true)
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Create new group")))
            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .check(matches(withText("Create new group")))
            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test for rename vehicle group visibility`() {
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, false)
            putParcelable(ConstantsTest.DATA, VehicleGroupResponse("1234", "Group1", "10"))
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Rename group")))
            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .check(matches(withText("Continue")))
            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.edVehicleGroup)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test cancel button`() {
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, false)
            putParcelable(ConstantsTest.DATA, VehicleGroupResponse("1234", "Group1", "10"))
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Rename group")))
            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .check(matches(withText("Continue")))
            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))

            onView(withId(R.id.cancelBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            Mockito.verify(navController).popBackStack()
        }
    }

    @Test
    fun `test for create new group api for success`() {
        every { viewModel.addVehicleGroupApiVal } returns createVehicleGroupLiveData
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, true)
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Create new group")))
            onView(withId(R.id.edVehicleGroup)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("New group"))

            shadowOf(getMainLooper()).idle()
            every { viewModel.addVehicleGroupApiVal } returns createVehicleGroupLiveData

            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            createVehicleGroupLiveData.postValue(
                Resource.Success(
                    VehicleGroupMngmtResponse(
                        true,
                        "",
                        ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals("group created successfully", ShadowToast.getTextOfLatestToast())
            Mockito.verify(navController)
                .navigate(R.id.action_createAndRenameVehicleGroupFragment_to_vehicleGroupMngmtFragment)

        }
    }

    @Test
    fun `test for create new group api for unknown error`() {
        every { viewModel.addVehicleGroupApiVal } returns createVehicleGroupLiveData
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, true)
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Create new group")))
            onView(withId(R.id.edVehicleGroup)).check(matches(isDisplayed()))
                .perform(ViewActions.typeText("New group"))

            shadowOf(getMainLooper()).idle()
            every { viewModel.addVehicleGroupApiVal } returns createVehicleGroupLiveData

            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            createVehicleGroupLiveData.postValue(
                Resource.DataError(
                    "unknown error"
                )
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }

        }
    }

    @Test
    fun `test for rename group api for success`() {
        every { viewModel.renameVehicleGroupApiVal } returns renameVehicleGroupLiveData
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, false)
            putParcelable(ConstantsTest.DATA, VehicleGroupResponse("1234", "Group1", "10"))
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Rename group")))

            onView(withId(R.id.edVehicleGroup)).check(matches(isDisplayed()))
                .perform(ViewActions.clearText(), ViewActions.typeText("Renamed group"))

            shadowOf(getMainLooper()).idle()
            every { viewModel.renameVehicleGroupApiVal } returns renameVehicleGroupLiveData

            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            renameVehicleGroupLiveData.postValue(
                Resource.Success(
                    VehicleGroupMngmtResponse(
                        true,
                        "",
                        ""
                    )
                )
            )
            shadowOf(getMainLooper()).idle()
            assertEquals("group renamed successfully", ShadowToast.getTextOfLatestToast())
            Mockito.verify(navController)
                .navigate(R.id.action_createAndRenameVehicleGroupFragment_to_vehicleGroupMngmtFragment)

        }
    }

    @Test
    fun `test for rename group api for unknown error`() {
        every { viewModel.renameVehicleGroupApiVal } returns renameVehicleGroupLiveData
        val bundle = Bundle().apply {
            putBoolean(ConstantsTest.IS_CREATE_VEHICLE_GROUP, false)
            putParcelable(ConstantsTest.DATA, VehicleGroupResponse("1234", "Group1", "10"))
        }
        launchFragmentInHiltContainer<CreateAndRenameVehicleGroupFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvVehicleGroup)).check(matches(isDisplayed()))
                .check(matches(withText("Rename group")))

            onView(withId(R.id.edVehicleGroup)).check(matches(isDisplayed()))
                .perform(ViewActions.clearText(), ViewActions.typeText("Renamed group"))

            shadowOf(getMainLooper()).idle()
            every { viewModel.renameVehicleGroupApiVal } returns renameVehicleGroupLiveData

            onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            renameVehicleGroupLiveData.postValue(
                Resource.DataError(
                    "unknown error"
                )
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

}