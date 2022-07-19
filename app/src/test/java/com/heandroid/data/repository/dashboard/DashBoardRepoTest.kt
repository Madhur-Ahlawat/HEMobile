package com.heandroid.data.repository.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.heandroid.data.remote.ApiService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class DashBoardRepoTest {

    private var dashBoardRepo: DashBoardRepo? = null

    @Mock
    private lateinit var apiService: ApiService

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        dashBoardRepo = DashBoardRepo(apiService)
    }

    @Test
    fun `test get vehicle data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehicleData("0","0")
            ).thenReturn(null)
            dashBoardRepo?.let {
                assertEquals(
                    it.getVehicleData(), null
                )
            }
        }
    }

    @Test
    fun `test get alert messages for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAlertMessages("")
            ).thenReturn(null)
            dashBoardRepo?.let {
                assertEquals(
                    it.getAlertMessages(), null
                )
            }
        }
    }

    @Test
    fun `test get vehicle crossing history data api for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehicleCrossingHistoryData(null)
            ).thenReturn(null)
            dashBoardRepo?.let {
                assertEquals(
                    it.crossingHistoryApiCall(null), null
                )
            }
        }
    }
    @Test
    fun `test get account details data null`() {
        runTest {
            Mockito.`when`(
                apiService.getAccountDetailsData()
            ).thenReturn(null)
            dashBoardRepo?.let {
                assertEquals(
                    it.getAccountDetailsApiCall(), null
                )
            }
        }
    }
    @Test
    fun `test get threshold value for null`() {
        runTest {
            Mockito.`when`(
                apiService.getThresholdValue()
            ).thenReturn(null)
            dashBoardRepo?.let {
                assertEquals(
                    it.getThresholdAmountApiCAll(), null
                )
            }
        }
    }

}