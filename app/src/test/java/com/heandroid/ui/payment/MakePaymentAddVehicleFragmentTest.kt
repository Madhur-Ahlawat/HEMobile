//package com.heandroid.ui.payment
//
//import android.os.Bundle
//import android.os.Looper.getMainLooper
//import android.widget.Button
//import androidx.lifecycle.MutableLiveData
//import androidx.navigation.NavController
//import androidx.navigation.Navigation
//import androidx.navigation.testing.TestNavHostController
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.contrib.RecyclerViewActions
//import androidx.test.espresso.matcher.RootMatchers.isDialog
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.filters.LargeTest
//import com.google.android.material.textfield.TextInputEditText
//import com.heandroid.R
//import com.heandroid.data.model.account.RetrievePlateInfoDetails
//import com.heandroid.data.model.account.VehicleInfoDetails
//import com.heandroid.data.model.vehicle.PlateInfoResponse
//import com.heandroid.data.model.vehicle.VehicleInfoResponse
//import com.heandroid.data.model.vehicle.VehicleResponse
//import com.heandroid.ui.loader.ErrorDialog
//import com.heandroid.ui.vehicle.VehicleMgmtViewModel
//import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
//import com.heandroid.ui.vehicle.addvehicle.AddVehicleVRMDialog
//import com.heandroid.utils.BaseActions
//import com.heandroid.utils.common.ConstantsTest
//import com.heandroid.utils.common.Resource
//import com.heandroid.utils.data.DataFile
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
//import kotlinx.coroutines.test.runTest
//import org.hamcrest.Matchers.not
//import org.junit.Assert
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
//@LargeTest
//class MakePaymentAddVehicleFragmentTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @BindValue
//    @JvmField
//    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)
//
//    private val findVehicle = MutableLiveData<Resource<VehicleInfoDetails?>?>()
//    private val validVehicle = MutableLiveData<Resource<String?>?>()
//
//    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//
//    @Before
//    fun init() {
//        hiltRule.inject()
//        every { viewModel.findVehicleLiveData } returns findVehicle
//        every { viewModel.validVehicleLiveData } returns validVehicle
//    }
//
//    @Test
//    fun `test make payment add vehicle screen visibility`() {
//        val bundle = Bundle().apply {
//            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, 1)
//        }
//        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(bundle) {
//            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
//            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
//            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
//            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
//        }
//    }
//
//    @Test
//    fun `test make payment add vehicle screen, add vehicle for success`() {
//        val bundle = Bundle().apply {
//            putInt(ConstantsTest.VEHICLE_SCREEN_KEY, 1)
//        }
//        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(bundle) {
//            navController.setGraph(R.navigation.nav_graph_make_off_payment)
//            navController.setCurrentDestination(R.id.makePaymentAddVehicleFragment)
//            Navigation.setViewNavController(requireView(), navController)
//            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
//            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
//            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
//            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
//            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
//                .perform(click())
//
//            runTest {
//                val dialogFragment =
//                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleVRMDialog
//                assert(dialogFragment.dialog?.isShowing == true)
//                onView(withId(R.id.add_vrm_input)).inRoot(isDialog())
//                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
//                Espresso.closeSoftKeyboard()
//                delay(500)
//                dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.performClick()
//                shadowOf(getMainLooper()).idle()
//                findVehicle.postValue(
//                    Resource.Success(
//                        VehicleInfoDetails(
//                            RetrievePlateInfoDetails(
//                                "", "", "", "", ""
//                            )
//                        )
//                    )
//                )
//                shadowOf(getMainLooper()).idle()
//                validVehicle.postValue(Resource.Success("yes"))
//                shadowOf(getMainLooper()).idle()
//                Assert.assertEquals(
//                    requireActivity().findViewById<RecyclerView>(R.id.rvVehiclesList).adapter?.itemCount,
//                    1
//                )
//                delay(500)
//                onView(withId(R.id.rvVehiclesList))
//                    .perform(
//                        RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
//                            0, BaseActions.clickOnViewChild(R.id.delete_img)
//                        )
//                    )
//
//                Assert.assertEquals(
//                    requireActivity().findViewById<RecyclerView>(R.id.rvVehiclesList).adapter?.itemCount,
//                    0
//                )
//            }
//        }
//    }
//
////    @Test
////    fun `test make payment add vehicle screen, add vehicle and remove vehicle`() {
////        val bundle = Bundle().apply {
////            putBoolean(ConstantsTest.PAYMENT_ONE_OFF, true)
////        }
////        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(bundle) {
////            navController.setGraph(R.navigation.nav_graph_make_off_payment)
////            navController.setCurrentDestination(R.id.makePaymentAddVehicleFragment)
////            Navigation.setViewNavController(requireView(), navController)
////            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
////            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
////            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
////            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
////            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
////            (this as MakePaymentAddVehicleFragment).onAddClick(
////                VehicleResponse(
////                    PlateInfoResponse(),
////                    PlateInfoResponse("1234", "UK"),
////                    VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
////                    false
////                )
////            )
////            this.onAddClick(
////                VehicleResponse(
////                    PlateInfoResponse(),
////                    PlateInfoResponse("4567", "UK"),
////                    VehicleInfoResponse("TATA", "Harrier", "2020", color = "black"),
////                    false
////                )
////            )
////
////            runTest {
////                Assert.assertEquals(
////                    requireActivity().findViewById<RecyclerView>(R.id.rvVehiclesList).adapter?.itemCount,
////                    2
////                )
////                delay(500)
////                onView(withId(R.id.rvVehiclesList))
////                    .perform(
////                        RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
////                            0, BaseActions.clickOnViewChild(R.id.delete_img)
////                        )
////                    )
////                onView(withId(R.id.rvVehiclesList))
////                    .perform(
////                        RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
////                            0, BaseActions.clickOnViewChild(R.id.delete_img)
////                        )
////                    )
////
////                Assert.assertEquals(
////                    requireActivity().findViewById<RecyclerView>(R.id.rvVehiclesList).adapter?.itemCount,
////                    0
////                )
////            }
////        }
////    }
////
////    @Test
////    fun `test make payment add vehicle screen, add vehicle navigate to next screen`() {
////        val bundle = Bundle().apply {
////            putBoolean(ConstantsTest.PAYMENT_ONE_OFF, true)
////        }
////        launchFragmentInHiltContainer<MakePaymentAddVehicleFragment>(bundle) {
////            navController.setGraph(R.navigation.nav_graph_make_off_payment)
////            navController.setCurrentDestination(R.id.makePaymentAddVehicleFragment)
////            Navigation.setViewNavController(requireView(), navController)
////            onView(withId(R.id.add_vehicles_txt)).check(matches(isDisplayed()))
////            onView(withId(R.id.rvVehiclesList)).check(matches(not(isDisplayed())))
////            onView(withId(R.id.findVehicle)).check(matches(isDisplayed()))
////            onView(withId(R.id.add_upto_five_vehicles)).check(matches(isDisplayed()))
////            onView(withId(R.id.addVehicleBtn)).check(matches(isDisplayed()))
////                .perform(click())
////
////            runTest {
////                val dialogFragment =
////                    childFragmentManager.findFragmentByTag(AddVehicleDialog.TAG) as AddVehicleVRMDialog
////                assert(dialogFragment.dialog?.isShowing == true)
////                dialogFragment.dialog?.findViewById<Button>(R.id.switch_view)?.performClick()
////                onView(withId(R.id.add_vrm_input)).inRoot(isDialog())
////                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
////                Espresso.closeSoftKeyboard()
////                delay(500)
////                dialogFragment.dialog?.findViewById<Button>(R.id.add_vehicle_btn)?.performClick()
////                shadowOf(getMainLooper()).idle()
////                findVehicle.postValue(
////                    Resource.Success(
////                        VehicleInfoDetails(
////                            RetrievePlateInfoDetails(
////                                "", "", "", "", ""
////                            )
////                        )
////                    )
////                )
////                shadowOf(getMainLooper()).idle()
////                validVehicle.postValue(Resource.Success("yes"))
////                shadowOf(getMainLooper()).idle()
////            }
////            Assert.assertEquals(
////                navController.currentDestination?.id,
////                R.id.addVehicleDetailsFragment
////            )
////        }
////    }
//
//
//}