package com.conduent.nationalhighways.ui.bottomnav.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.ThresholdAmountApiResponse
import com.conduent.nationalhighways.data.model.account.ThresholdAmountData
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryResponse
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.utils.BaseActions
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
class DashboardFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<DashboardViewModel>(relaxed = true)

    private val vehicleList = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    private val crossingHistoryList = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()
    private val accountOverview = MutableLiveData<Resource<AccountResponse?>?>()
    private val alerts = MutableLiveData<Resource<AlertMessageApiResponse?>?>()
    private val thresholdAmount = MutableLiveData<Resource<ThresholdAmountApiResponse?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test dashboard screen visibility`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_number_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_status_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_top_up_heading)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test dashboard screen, navigate to vehicle list screen`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            navController.setGraph(R.navigation.bottom_navigation_graph)
            navController.setCurrentDestination(R.id.dashBoardFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_number_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_status_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_top_up_heading)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_view_vehicle)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.vehicleListFragment2
            )
        }
    }

    @Test
    fun `test dashboard screen, navigate to vehicle crossings screen`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            navController.setGraph(R.navigation.bottom_navigation_graph)
            navController.setCurrentDestination(R.id.dashBoardFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_view_crossings)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.crossingHistoryFragment
            )
        }
    }

    @Test
    fun `test dashboard screen, navigate to top up screen`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_number_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_status_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_top_up_heading)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up))
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())
        }
    }

    private fun getApiCallData() {
        every { viewModel.vehicleListVal } returns vehicleList
        every { viewModel.crossingHistoryVal } returns crossingHistoryList
        every { viewModel.getAlertsVal } returns alerts
        every { viewModel.accountOverviewVal } returns accountOverview
        every { viewModel.thresholdAmountVal } returns thresholdAmount
        val v1 = VehicleResponse(
            PlateInfoResponse(),
            PlateInfoResponse("1234"),
            VehicleInfoResponse(),
            false
        )
        val v2 = VehicleResponse(
            PlateInfoResponse(),
            PlateInfoResponse("ABCD"),
            VehicleInfoResponse(),
            false
        )
        val vehiclesList = listOf(v1, v2)
        vehicleList.postValue(Resource.Success(vehiclesList))
        val item1 = DataFile.getCrossingHistoryItem("1234")
        val item2 = DataFile.getCrossingHistoryItem("ABCD")
        val crossingList = mutableListOf(item1, item2)
        val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
        val crossingHistoryResponse =
            CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")
        crossingHistoryList.postValue(Resource.Success(crossingHistoryResponse))
        accountOverview.postValue(Resource.Success(DataFile.getAccountResponse()))
        val messageList = listOf(DataFile.getAlertMessage(), DataFile.getAlertMessage())
        alerts.postValue(Resource.Success(AlertMessageApiResponse(200, "message", messageList)))
        thresholdAmount.postValue(
            Resource.Success(
                ThresholdAmountApiResponse(
                    ThresholdAmountData(
                        "50",
                        "150"
                    ), "", ""
                )
            )
        )
    }
}