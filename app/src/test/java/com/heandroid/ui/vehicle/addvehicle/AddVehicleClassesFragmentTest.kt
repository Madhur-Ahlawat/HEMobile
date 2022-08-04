package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.os.Looper.getMainLooper
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.dialog.VehicleAddConfirmDialog
import com.heandroid.utils.BaseActions.betterScrollTo
import com.heandroid.utils.BaseActions.forceClick
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
class AddVehicleClassesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    private val addVehicleLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        val vehicle = VehicleResponse(
            PlateInfoResponse(),
            PlateInfoResponse("L062 1234", "UK"),
            VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
            false
        )
        bundle = Bundle().apply {
            putParcelable(ConstantsTest.DATA, vehicle)
            putBoolean(ConstantsTest.PAYMENT_PAGE, false)
        }
        hiltRule.inject()
    }

    @Test
    fun `test add vehicle class type visibility`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classBView)).check(matches(isDisplayed()))

            onView(withId(R.id.classADesc)).check(matches(isDisplayed()))
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test add vehicle class type, select class A`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classA_RadioButton)).check(matches(isDisplayed()))
                .perform(forceClick())
            onView(withId(R.id.classA_RadioButton)).check(matches(isChecked()))
               onView(withId(R.id.classADesc)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test add vehicle class type, select class B`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classBView)).check(matches(isDisplayed()))
            onView(withId(R.id.classB_RadioButton)).check(matches(isDisplayed()))
                .perform(forceClick())
            onView(withId(R.id.classB_RadioButton)).check(matches(isChecked()))
            onView(withId(R.id.classBDesc)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test add vehicle class type, select class C`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classCView))
                .perform(betterScrollTo()).check(matches(isDisplayed()))
            onView(withId(R.id.classC_RadioButton))
                .perform(betterScrollTo()).check(matches(isDisplayed()))
                .perform(forceClick())
            onView(withId(R.id.classC_RadioButton)).check(matches(isChecked()))
            onView(withId(R.id.classCDesc))
                .perform(betterScrollTo()).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test add vehicle class type, select class D`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classDView))
                .perform(betterScrollTo()).check(matches(isDisplayed()))
            onView(withId(R.id.classD_RadioButton))
                .perform(betterScrollTo()).check(matches(isDisplayed()))
                .perform(forceClick())
            onView(withId(R.id.classD_RadioButton)).check(matches(isChecked()))
            onView(withId(R.id.classDDesc))
                .perform(betterScrollTo()).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test add vehicle class type, cancel button`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
                .perform(click())

            Mockito.verify(navController).popBackStack()
        }
    }

    @Test
    fun `test add vehicle class type, next button, add vehicle success`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classVehicleCheckbox)).check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
                .perform(click())

            val dialogFragment =
                childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as VehicleAddConfirmDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.yes_btn)?.performClick()

            val emptyApiResponse = Mockito.mock(EmptyApiResponse::class.java)
            addVehicleLiveData.postValue(Resource.Success(emptyApiResponse))

//            Mockito.verify(navController).navigate(R.id.action_addVehicleClassesFragment_to_addVehicleDoneFragment, bun)
        }
    }

    @Test
    fun `test add vehicle class type, next button, add vehicle failure`() {
        every { viewModel.addVehicleApiVal } returns addVehicleLiveData

        launchFragmentInHiltContainer<AddVehicleClassesFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classVehicleCheckbox)).check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
                .perform(click())

            val dialogFragment =
                childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as VehicleAddConfirmDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.yes_btn)?.performClick()
            every { viewModel.addVehicleApiVal } returns addVehicleLiveData
            addVehicleLiveData.postValue(Resource.DataError("Unknown error"))
            shadowOf(getMainLooper()).idle()

            runTest {
                delay(1000)
                val dialogFragment2 =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment2.dialog?.isShowing == true)
            }

        }
    }


}