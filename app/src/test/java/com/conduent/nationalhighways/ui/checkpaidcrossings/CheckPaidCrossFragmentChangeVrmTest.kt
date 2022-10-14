package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.os.Looper.getMainLooper
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferResponse
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.conduent.nationalhighways.ui.checkpaidcrossings.dialog.ConfirmChangeDialog
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class CheckPaidCrossFragmentChangeVrmTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CheckPaidCrossingViewModel>(relaxed = true)

    private val balanceTransferLiveData = MutableLiveData<Resource<BalanceTransferResponse?>?>()
    private val paidCrossingLiveData = MutableLiveData<CheckPaidCrossingsOptionsModel?>()
    private val paidCrossing = CheckPaidCrossingsOptionsModel(
        "112233", "qq", true)
    private val paidCrossingResponseLiveData = MutableLiveData<CheckPaidCrossingsResponse?>()
    private val paidCrossingResponse = CheckPaidCrossingsResponse(
        "112233", "qq", "type", "qq",
        "cd", "UK","100", "2022" )


    private lateinit var bundle: Bundle
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putString(Constants.COUNTRY_TYPE, "UK")
            putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, "12345")
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
            putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, VehicleInfoDetails(null))
        }
        every { viewModel.balanceTransfer } returns balanceTransferLiveData
        every { viewModel.paidCrossingOption } returns paidCrossingLiveData
        every { viewModel.paidCrossingResponse } returns paidCrossingResponseLiveData
    }

    @Test
    fun `test paid crossing for existed vehicle`() {
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test paid crossing for non existed vehicle`() {
        bundle.apply {
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
        }
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicle)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.addVehicleDetailsFragment
            )
        }
    }

    @Test
    fun `test paid crossing for non existed vehicle, test remove button`() {
        bundle.apply {
            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
        }
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.removeVehicle)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            assertEquals(
                navController.currentDestination?.id,
                R.id.enterVrmFragment
            )
        }
    }

    @Test
    fun `test paid crossings balance transfer for success api call`() {
        paidCrossingLiveData.value = paidCrossing
        paidCrossingResponseLiveData.value = paidCrossingResponse
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            val dialogFragment =
                childFragmentManager.findFragmentByTag(Constants.DELETE_VEHICLE_GROUP_DIALOG) as ConfirmChangeDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.btnConfirm)?.performClick()
            assert(dialogFragment.dialog?.isShowing == false)
//            (this@launchFragmentInHiltContainer as CheckPaidCrossFragmentChangeVrm).onConfirmClick()
            shadowOf(getMainLooper()).idle()
            balanceTransferLiveData.postValue(Resource.Success(BalanceTransferResponse(true)))
            shadowOf(getMainLooper()).idle()
            assertEquals(
                navController.currentDestination?.id,
                R.id.checkPaidCrossingChangeVrmConformSuccess
            )
        }
    }

    @Test
    fun `test paid crossings balance transfer for failed api call`() {
        paidCrossingLiveData.value = paidCrossing
        paidCrossingResponseLiveData.value = paidCrossingResponse
        launchFragmentInHiltContainer<CheckPaidCrossFragmentChangeVrm>(bundle) {
            navController.setGraph(R.navigation.nav_check_paid_crossings)
            navController.setCurrentDestination(R.id.checkPaidCrossingChangeVrm)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.regNum)).check(matches(isDisplayed()))
            onView(withId(R.id.countryMarker)).check(matches(isDisplayed()))
            onView(withId(R.id.changeVehicle)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            val dialogFragment =
                childFragmentManager.findFragmentByTag(Constants.DELETE_VEHICLE_GROUP_DIALOG) as ConfirmChangeDialog
            assert(dialogFragment.dialog?.isShowing == true)
            dialogFragment.dialog?.findViewById<Button>(R.id.btnConfirm)?.performClick()
            assert(dialogFragment.dialog?.isShowing == false)
//            (this@launchFragmentInHiltContainer as CheckPaidCrossFragmentChangeVrm).onConfirmClick()
            shadowOf(getMainLooper()).idle()
            balanceTransferLiveData.postValue(Resource.DataError("unknown error"))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment2 =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment2.dialog?.isShowing == true)
            }
        }
    }

}