package com.heandroid.ui.bottomnav.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.account.ThresholdAmountData
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryResponse
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.utils.BaseActions
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
import org.bouncycastle.jcajce.provider.symmetric.ARC4
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
@LargeTest
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
    private val navController: NavController = Mockito.mock(NavController::class.java)

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
            onView(withId(R.id.tv_account_type)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test dashboard screen, navigate to vehicle list screen`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_number_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_status_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_account_type)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_view_vehicle)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())

//            Mockito.verify(navController).navigate(R.id.action_dashBoardFragment_to_vehicleListFragment2, bundle)
        }
    }

    //Test
    fun `test dashboard screen, navigate to vehicle crossings screen`() {
        getApiCallData()
        launchFragmentInHiltContainer<DashboardFragment> {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tv_available_balance_heading)).check(matches(isDisplayed()))
            onView(withId(R.id.rv_notification)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_manual_top_up)).check(matches(isDisplayed()))
            onView(withId(R.id.crossingsView)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed())).perform(BaseActions.forceClick())

            Mockito.verify(navController).navigate(R.id.action_dashBoardFragment_to_crossingHistoryFragment)
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
            onView(withId(R.id.tv_account_type)).check(matches(isDisplayed()))
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
        val messageList = listOf(DataFile.getAlertMessage(),DataFile.getAlertMessage() )
        alerts.postValue(Resource.Success(AlertMessageApiResponse(200, "message", messageList)))
        thresholdAmount.postValue(Resource.Success(ThresholdAmountApiResponse(ThresholdAmountData("50", "150"), "", "")))
    }
}