package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AddVehicleDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController: NavController = Mockito.mock(NavController::class.java)
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        val vehicle = VehicleResponse(
            PlateInfoResponse(),
            PlateInfoResponse("L062 1234", "UK"),
            VehicleInfoResponse(),
            false
        )
        bundle = Bundle().apply {
            putParcelable(ConstantsTest.DATA, vehicle)
            putBoolean(ConstantsTest.PAYMENT_PAGE, false)
        }
        hiltRule.inject()
    }

    @Test
    fun `test vehicle details screen visibility`() {
        launchFragmentInHiltContainer<AddVehicleDetailsFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.subTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.next_btn)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withText("Country of registration UK")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test vehicle details screen, adding make, model, color`() {
        launchFragmentInHiltContainer<AddVehicleDetailsFragment>(bundle) {
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.subTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.next_btn)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withText("Country of registration UK")).check(matches(isDisplayed()))

            onView(withId(R.id.makeInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("TATA"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.modelInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("Harrier"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.colorInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("Black"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.next_btn)).check(matches(isClickable()))
        }
    }

    @Test
    fun `test vehicle details screen, next button click`() {
        launchFragmentInHiltContainer<AddVehicleDetailsFragment>(bundle) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.subTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.next_btn)).check(matches(isDisplayed()))
            onView(withText("Vehicle registration number: L062 1234")).check(matches(isDisplayed()))
            onView(withText("Country of registration UK")).check(matches(isDisplayed()))

            onView(withId(R.id.makeInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("TATA"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.modelInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("Harrier"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.colorInputEditText)).perform(
                ViewActions.click(),
                ViewActions.typeText("Black"),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.next_btn)).check(matches(isClickable())).perform(ViewActions.click())
//            Mockito.verify(navController).navigate(R.id.addVehicleDetailsFragment, bundle)
        }
    }


}