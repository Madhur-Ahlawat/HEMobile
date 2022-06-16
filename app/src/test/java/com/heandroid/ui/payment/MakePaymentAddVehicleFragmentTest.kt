//package com.heandroid.ui.payment
//
//import android.os.Bundle
//import android.os.Looper.getMainLooper
//import android.widget.Button
//import androidx.fragment.app.testing.launchFragment
//import androidx.lifecycle.MutableLiveData
//import androidx.navigation.NavController
//import androidx.navigation.Navigation
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.action.ViewActions.*
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.contrib.RecyclerViewActions
//import androidx.test.espresso.matcher.RootMatchers.isDialog
//import androidx.test.espresso.matcher.ViewMatchers.*
//import com.google.android.material.textfield.TextInputEditText
//import com.heandroid.R
//import com.heandroid.data.model.vehicle.PlateInfoResponse
//import com.heandroid.data.model.vehicle.VehicleInfoResponse
//import com.heandroid.data.model.vehicle.VehicleResponse
//import com.heandroid.ui.loader.ErrorDialog
//import com.heandroid.ui.vehicle.VehicleMgmtViewModel
//import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
//import com.heandroid.utils.BaseActions
//import com.heandroid.utils.BaseActions.atPosition
//import com.heandroid.utils.BaseActions.clickOnViewChild
//import com.heandroid.utils.common.Constants
//import com.heandroid.utils.common.ConstantsTest
//import com.heandroid.utils.common.Resource
//import com.heandroid.utils.launchFragmentInHiltContainer
//import dagger.hilt.android.testing.BindValue
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import dagger.hilt.android.testing.HiltTestApplication
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.test.runBlockingTest
//import org.hamcrest.Matchers.not
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertFalse
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.Shadows.shadowOf
//import org.robolectric.annotation.Config
//
//@ExperimentalCoroutinesApi
//@HiltAndroidTest
//@Config(application = HiltTestApplication::class)
//@RunWith(RobolectricTestRunner::class)
//class MakePaymentAddVehicleFragmentTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @BindValue
//    @JvmField
//    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)
//
//    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
//
//    private val navController: NavController = Mockito.mock(NavController::class.java)
//
//    @Before
//    fun init() {
//        hiltRule.inject()
//    }
//
//    @Test
//    fun `test make payment add vehicle screen visibility`() {
//        val bundle = Bundle().apply {
//            putBoolean(ConstantsTest.PAYMENT_ONE_OFF, true)
//        }
//        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(
//            bundle
//        ) {
//            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
//            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
//            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
//            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
//        }
//    }
//
//    @Test
//    fun `test make payment add vehicle screen, add vehicle`() {
//        val bundle = Bundle().apply {
//            putBoolean(ConstantsTest.PAYMENT_ONE_OFF, true)
//        }
//        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(
//            bundle
//        ) {
//            Navigation.setViewNavController(requireView(), navController)
//            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
//            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
//            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
//            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
//                .perform(click())
//
//            shadowOf(getMainLooper()).idle()
//            runBlockingTest {
//                val dialogFragment =
//                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleDialog
//                assert(dialogFragment.dialog?.isShowing == true)
//                dialogFragment.dialog?.findViewById<Button>(R.id.switch_view)?.performClick()
//                dialogFragment.dialog?.findViewById<TextInputEditText>(R.id.add_vrm_input)?.setText("L062 NRO")
//                dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.performClick()
//                assert(dialogFragment.dialog?.isShowing == false)
//            }
//
////            Mockito.verify(navController).navigate(R.id.action_makePaymentAddVehicleFragment_to_addVehicleDetailsFragment, bundle)
//        }
//    }
//
//
//
//
//
//
//
//
//
//}