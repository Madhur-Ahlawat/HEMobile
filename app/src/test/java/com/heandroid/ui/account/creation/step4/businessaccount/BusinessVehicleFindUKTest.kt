package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
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
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class BusinessVehicleFindUKTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountVehicleViewModel>(relaxed = true)

    private val findVehicleLiveData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    private val validVehicleLiveData = MutableLiveData<Resource<String?>?>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = null
                }
            )

        }
    }

    @Test
    fun `test business vehicle find Uk screen visibility`() {
        every { viewModel.findVehicleLiveData } returns findVehicleLiveData
        every { viewModel.validVehicleLiveData } returns validVehicleLiveData
        launchFragmentInHiltContainer<BusinessVehicleFindUK>(bundle) {
            onView(withId(R.id.vehicleParent)).check(matches(isDisplayed()))
            onView(withId(R.id.addTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.findVehicleBusiness)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test click find your vehicle button for non duplicate vehicle`() {
        every { viewModel.findVehicleLiveData } returns findVehicleLiveData
        every { viewModel.validVehicleLiveData } returns validVehicleLiveData
        launchFragmentInHiltContainer<BusinessVehicleFindUK>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleUKListFragment)
            Navigation.setViewNavController(requireView(), navController)

            runTest {
                shadowOf(getMainLooper()).idle()
                onView(withId(R.id.findVehicleBusiness)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                shadowOf(getMainLooper()).idle()
                findVehicleLiveData.postValue(
                    Resource.Success(
                        VehicleInfoDetails(
                            RetrievePlateInfoDetails(
                                "1234", "4",
                                "make", "model", "black"
                            )
                        )
                    )
                )
                shadowOf(getMainLooper()).idle()
                validVehicleLiveData.postValue(Resource.Success("no"))
                shadowOf(getMainLooper()).idle()
            }

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                        ftvehicleList = null
                    }
                )
                putParcelable(
                    ConstantsTest.NON_UK_VEHICLE_DATA,
                    NonUKVehicleModel(
                        vehicleColor = "black",
                        vehicleMake = "make",
                        vehicleModel = "model",
                        vehicleClassDesc = "D"

                    )
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test click find your vehicle for unknown error`() {
        every { viewModel.findVehicleLiveData } returns findVehicleLiveData
        every { viewModel.validVehicleLiveData } returns validVehicleLiveData
        launchFragmentInHiltContainer<BusinessVehicleFindUK>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleUKListFragment)
            Navigation.setViewNavController(requireView(), navController)
            shadowOf(getMainLooper()).idle()
            runTest {
                shadowOf(getMainLooper()).idle()
                onView(withId(R.id.findVehicleBusiness)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                shadowOf(getMainLooper()).idle()
                findVehicleLiveData.postValue(
                    Resource.DataError(
                        "unknown error"
                    )
                )
                shadowOf(getMainLooper()).idle()
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessNonUKMakeFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                        ftvehicleList = null
                        correspDeliveryFrequency = null
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test click find your vehicle button for non duplicate vehicle with unknown error`() {
        every { viewModel.findVehicleLiveData } returns findVehicleLiveData
        every { viewModel.validVehicleLiveData } returns validVehicleLiveData
        launchFragmentInHiltContainer<BusinessVehicleFindUK>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleUKListFragment)
            Navigation.setViewNavController(requireView(), navController)

            runTest {
                shadowOf(getMainLooper()).idle()
                onView(withId(R.id.findVehicleBusiness)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
                shadowOf(getMainLooper()).idle()
                findVehicleLiveData.postValue(
                    Resource.Success(
                        VehicleInfoDetails(
                            RetrievePlateInfoDetails(
                                "1234", "4",
                                "make", "model", "black"
                            )
                        )
                    )
                )
                shadowOf(getMainLooper()).idle()
                validVehicleLiveData.postValue(Resource.DataError("no"))
                shadowOf(getMainLooper()).idle()
            }

            Assert.assertNotEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleUKListFragment
            )

        }
    }


}