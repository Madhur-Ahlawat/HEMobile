package com.conduent.nationalhighways.ui.vehicle.crossinghistory

import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryItem
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.BaseActions
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
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
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
class CrossingHistoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)

    private val crossingHistoryLiveData = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()
    private val crossingHistoryDownloadLiveData = MutableLiveData<Resource<ResponseBody?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test crossing history screen visibility`() {
        every { viewModel.crossingHistoryVal } returns crossingHistoryLiveData
        launchFragmentInHiltContainer<CrossingHistoryFragment> {
            onView(withId(R.id.appCompatTextView3)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val crossingList = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            crossingHistoryLiveData.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvHistory).adapter?.itemCount,
                crossingHistoryResponse.transactionList?.transaction?.size
            )
        }
    }

    @Test
    fun `test crossing history screen visibility for no crossings`() {
        every { viewModel.crossingHistoryVal } returns crossingHistoryLiveData
        val crossingList = mutableListOf<CrossingHistoryItem?>()
        val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
        val crossingHistoryResponse =
            CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

        crossingHistoryLiveData.postValue(Resource.Success(crossingHistoryResponse))
        launchFragmentInHiltContainer<CrossingHistoryFragment> {
            onView(withId(R.id.appCompatTextView3)).check(matches(isDisplayed()))

            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvHistory).adapter?.itemCount,
                crossingHistoryResponse.transactionList?.transaction?.size
            )
        }
    }

    @Test
    fun `test crossing history for unknown error`() {
        every { viewModel.crossingHistoryVal } returns MutableLiveData(Resource.DataError("Unknown Error"))
        crossingHistoryLiveData.postValue(Resource.DataError("Unknown Error"))
        launchFragmentInHiltContainer<CrossingHistoryFragment> {
            onView(withId(R.id.appCompatTextView3)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            crossingHistoryLiveData.postValue(Resource.DataError("Unknown Error"))

            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test crossing history screen, crossing history item detail`() {
        every { viewModel.crossingHistoryVal } returns crossingHistoryLiveData
        launchFragmentInHiltContainer<CrossingHistoryFragment> {
            navController.setGraph(R.navigation.navigation_crossing_history)
            navController.setCurrentDestination(R.id.crossingHistoryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.appCompatTextView3)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val crossingList = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")

            crossingHistoryLiveData.postValue(Resource.Success(crossingHistoryResponse))
            onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))

            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.rvHistory).adapter?.itemCount,
                crossingHistoryResponse.transactionList?.transaction?.size
            )
            onView(withId(R.id.rvHistory))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0, BaseActions.clickOnViewChild(R.id.tv_status)
                    )
                )
            assertEquals(
                navController.currentDestination?.id,
                R.id.crossingHistoryMakePaymentFragment
            )
        }
    }

//    @Test
//    fun `test crossing history download for success`() {
//        val responseBody = Mockito.mock(ResponseBody::class.java)
//        every { viewModel.crossingHistoryVal } returns crossingHistoryLiveData
//        every { viewModel.crossingHistoryDownloadVal } returns crossingHistoryDownloadLiveData
//        launchFragmentInHiltContainer<CrossingHistoryFragment> {
//            onView(withId(R.id.appCompatTextView3)).check(matches(isDisplayed()))
//            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
//            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))
//            shadowOf(getMainLooper()).idle()
//
//            val item1 = DataFile.getCrossingHistoryItem("1234")
//            val item2 = DataFile.getCrossingHistoryItem("ABCD")
//            val crossingList = mutableListOf(item1, item2)
//            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
//            val crossingHistoryResponse =
//                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")
//
//            crossingHistoryLiveData.postValue(Resource.Success(crossingHistoryResponse))
//            onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))
//
//            assertEquals(
//                requireActivity().findViewById<RecyclerView>(R.id.rvHistory).adapter?.itemCount,
//                crossingHistoryResponse.transactionList?.transaction?.size
//            )
//            onView(withId(R.id.tvDownload)).perform(ViewActions.click())
//            crossingHistoryDownloadLiveData.postValue(Resource.Success(responseBody))
//            assertEquals("Document download failed", ShadowToast.getTextOfLatestToast())
//        }
//    }


}
