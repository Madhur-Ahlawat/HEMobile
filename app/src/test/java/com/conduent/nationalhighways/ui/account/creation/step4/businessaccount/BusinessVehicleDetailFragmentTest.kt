package com.conduent.nationalhighways.ui.account.creation.step4.businessaccount

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountVehicleListModel
import com.conduent.nationalhighways.data.model.account.CreateAccountVehicleModel
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.ui.account.creation.step5.businessaccount.BusinessVehicleDetailFragment
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@MediumTest
class BusinessVehicleDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.NON_UK_VEHICLE_DATA,
                NonUKVehicleModel().apply {
                    vehicleClass = "4"
                    vehicleColor = "black"
                    plateCountry = "UK"
                    vehicleYear = "2023"
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
    }

    @Test
    fun `test vehicle details screen visibility for personal account`() {
        launchFragmentInHiltContainer<BusinessVehicleDetailFragment>(bundle) {
            onView(withId(R.id.addTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.separator)).check(matches(isDisplayed()))
            onView(withId(R.id.buttonsView)).check(matches(isDisplayed()))
            onView(withId(R.id.confirmBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.notVehicle)).check(matches(isDisplayed()))
        }
    }

/*
    @Test
    fun `test vehicle details screen ,click not your vehicle button`() {
        launchFragmentInHiltContainer<BusinessVehicleDetailFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleDetailFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.buttonsView)).check(matches(isDisplayed()))
            onView(withId(R.id.confirmBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.notVehicle)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
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
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
*/

    @Test
    fun `test vehicle details screen ,navigate to next screen`() {
        launchFragmentInHiltContainer<BusinessVehicleDetailFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleDetailFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.confirmBtn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.paymentSummaryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                        ftvehicleList = CreateAccountVehicleListModel(
                            vehicle = mutableListOf(
                                CreateAccountVehicleModel(
                                    "", "STANDARD", "black", "",
                                    "", "", "", "2022", "HE",
                                    "4", ""
                                )
                            )
                        )
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test vehicle details screen ,navigate to next for business account`() {
        val bund = Bundle().apply {
            putParcelable(
                Constants.NON_UK_VEHICLE_DATA,
                NonUKVehicleModel().apply {
                    vehicleClass = "4"
                    vehicleColor = "black"
                    plateCountry = "UK"
                    vehicleYear = "2023"
                }
            )
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
        }
        launchFragmentInHiltContainer<BusinessVehicleDetailFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.businessVehicleDetailFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.confirmBtn)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.paymentSummaryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                        ftvehicleList = CreateAccountVehicleListModel(
                            vehicle = mutableListOf(
                                CreateAccountVehicleModel(
                                    "", "STANDARD", "black", "",
                                    "", "", "", "2022", "HE",
                                    "4", ""
                                )
                            )
                        )
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
}