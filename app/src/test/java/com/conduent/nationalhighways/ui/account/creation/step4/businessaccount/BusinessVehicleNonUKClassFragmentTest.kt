package com.conduent.nationalhighways.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.account.creation.step5.businessaccount.dialog.BusinessAddConfirmDialog
import com.conduent.nationalhighways.ui.account.creation.step5.businessaccount.BusinessVehicleNonUKClassFragment
import com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog.VehicleAddConfirmDialog
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
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
class BusinessVehicleNonUKClassFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountVehicleViewModel>(relaxed = true)

    private val validVehicle = MutableLiveData<Resource<String?>?>()

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
            putParcelable(
                ConstantsTest.NON_UK_VEHICLE_DATA,
                NonUKVehicleModel()
            )
        }
        every { viewModel.validVehicleLiveData } returns validVehicle
    }

    @Test
    fun `test business vehicle non uk screen visibility`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.vehicleRegNum)).check(matches(isDisplayed()))
            onView(withId(R.id.classAView)).check(matches(isDisplayed()))
            onView(withId(R.id.classBView)).check(matches(isDisplayed()))
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test business vehicle non uk screen, navigate to next screen with class A`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKClassFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.classB_RadioButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.classA_RadioButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            onView(withId(R.id.cbDeclare)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.yes_btn)).inRoot(RootMatchers.isDialog())
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.NON_UK_VEHICLE_DATA,
                    NonUKVehicleModel().apply {
                        vehicleClassDesc = "A"
                    }
                )
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business vehicle non uk screen, navigate to next screen with class B`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKClassFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.classB_RadioButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.cbDeclare)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.yes_btn)).inRoot(RootMatchers.isDialog())
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.NON_UK_VEHICLE_DATA,
                    NonUKVehicleModel().apply {
                        vehicleClassDesc = "B"
                    }
                )
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business vehicle non uk screen, navigate to next screen with class C`() {
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKClassFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.classC_RadioButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.cbDeclare)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.yes_btn)).inRoot(RootMatchers.isDialog())
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.NON_UK_VEHICLE_DATA,
                    NonUKVehicleModel().apply {
                        vehicleClassDesc = "C"
                    }
                )
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business vehicle non uk screen, navigate to next screen with class D for business account`() {
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
            putParcelable(
                ConstantsTest.NON_UK_VEHICLE_DATA,
                NonUKVehicleModel()
            )
        }
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKClassFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.classD_RadioButton)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onView(withId(R.id.cbDeclare)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            runTest {
                onView(withId(R.id.groupName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("group name"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.yes_btn)).inRoot(RootMatchers.isDialog())
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessVehicleDetailFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.NON_UK_VEHICLE_DATA,
                    NonUKVehicleModel().apply {
                        vehicleClassDesc = "D"
                        vehicleGroup = "group name"
                    }
                )
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test business vehicle non uk screen, test confirm dialog dismiss`() {
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
            putParcelable(
                ConstantsTest.NON_UK_VEHICLE_DATA,
                NonUKVehicleModel()
            )
        }
        launchFragmentInHiltContainer<BusinessVehicleNonUKClassFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessNonUKClassFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.cbDeclare)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            runTest {
                onView(withId(R.id.groupName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("group name"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.cancel_btn)).inRoot(RootMatchers.isDialog())
                    .perform(ViewActions.click())
            }

            onView(withId(R.id.continueButton)).perform(BaseActions.betterScrollTo())
                .perform(BaseActions.forceClick())
            shadowOf(getMainLooper()).idle()
            validVehicle.postValue(Resource.Success(""))
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    childFragmentManager.findFragmentByTag(VehicleAddConfirmDialog.TAG) as BusinessAddConfirmDialog
                assert(dialogFragment.dialog?.isShowing == true)
                onView(withId(R.id.ivClose)).inRoot(RootMatchers.isDialog())
                    .perform(BaseActions.forceClick())
            }
        }
    }
}