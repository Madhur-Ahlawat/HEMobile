package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.ui.account.creation.step5.businessaccount.BusinessVehicleFindUK
import com.heandroid.ui.account.creation.step5.CreateAccountFindVehicleFragment
import com.heandroid.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.common.Resource
import com.heandroid.utils.data.DataFile
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class CreateAccountFindVehicleFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountVehicleViewModel>(relaxed = true)

    private val findLiveData = MutableLiveData<Resource<VehicleInfoDetails?>?>()

    private val validateLiveData = MutableLiveData<Resource<String?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel()
            )
        }
    }

    @Test
    fun `test create account find vehicle screen visibility`() {
        every { viewModel.findVehicleLiveData } returns findLiveData
        every { viewModel.validVehicleLiveData } returns validateLiveData
        launchFragmentInHiltContainer<CreateAccountFindVehicleFragment>(bundle) {
            onView(withId(R.id.tv_verification)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_step)).check(matches(isDisplayed()))
            onView(withId(R.id.materialCardView)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_vehicle_reg_no)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account find vehicle screen, navigate to next screen`() {
        every { viewModel.findVehicleLiveData } returns findLiveData
        every { viewModel.validVehicleLiveData } returns validateLiveData
        launchFragmentInHiltContainer<CreateAccountFindVehicleFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.findYourVehicleFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_verification)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_step)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_vehicle_reg_no)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
            onView(withId(R.id.materialCardView)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.add_vrm_input)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }

            onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleUKListFragment
            )
        }
    }

    @Test
    fun `test click find your vehicle button for non duplicate vehicle`() {
        every { viewModel.findVehicleLiveData } returns findLiveData
        every { viewModel.validVehicleLiveData } returns validateLiveData
        launchFragmentInHiltContainer<CreateAccountFindVehicleFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.findYourVehicleFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            runTest {
                onView(withId(R.id.add_vrm_input)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            runTest {
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                findLiveData.postValue(
                    Resource.Success(
                        VehicleInfoDetails(
                            RetrievePlateInfoDetails(
                                "1234", "4",
                                "make", "model", "black"
                            )
                        )
                    )
                )
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                validateLiveData.postValue(Resource.Success("no"))
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
        }
    }

    @Test
    fun `test click find your vehicle for unknown error`() {
        every { viewModel.findVehicleLiveData } returns findLiveData
        every { viewModel.validVehicleLiveData } returns validateLiveData
        launchFragmentInHiltContainer<CreateAccountFindVehicleFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.findYourVehicleFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            runTest {
                onView(withId(R.id.add_vrm_input)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            runTest {
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                findLiveData.postValue(
                    Resource.DataError(
                        "unknown error"
                    )
                )
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessNonUKMakeFragment
            )
        }
    }

    @Test
    fun `test click find your vehicle button for non duplicate vehicle with unknown error`() {
        every { viewModel.findVehicleLiveData } returns findLiveData
        every { viewModel.validVehicleLiveData } returns validateLiveData
        launchFragmentInHiltContainer<CreateAccountFindVehicleFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.findYourVehicleFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.switch_view)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            runTest {
                onView(withId(R.id.add_vrm_input)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("N062 1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            runTest {
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                onView(withId(R.id.continue_btn)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                findLiveData.postValue(
                    Resource.Success(
                        VehicleInfoDetails(
                            RetrievePlateInfoDetails(
                                "1234", "4",
                                "make", "model", "black"
                            )
                        )
                    )
                )
                Shadows.shadowOf(Looper.getMainLooper()).idle()
                validateLiveData.postValue(Resource.DataError("no"))
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }

            Assert.assertNotEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
            )
        }
    }

}