package com.conduent.nationalhighways.data.repository.checkpaidcrossings


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.conduent.nationalhighways.data.remote.ApiService
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
class CheckPaidCrossingsRepoTest {

    private var checkPaidCrossingsRepo: CheckPaidCrossingsRepo? = null

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
        checkPaidCrossingsRepo = CheckPaidCrossingsRepo(apiService)
    }

    @Test
    fun `test login with ref and plate number for null`() {
        runTest {
            Mockito.`when`(
                apiService.loginWithRefAndPlateNumber(null, true, "")
            ).thenReturn(null)
            checkPaidCrossingsRepo?.let {
                assertEquals(
                    it.loginWithRefAndPlateNumber(null), null
                )
            }
        }
    }


    @Test
    fun `test balance transfer for null`() {
        runTest {
            Mockito.`when`(
                apiService.balanceTransfer(null)
            ).thenReturn(null)
            checkPaidCrossingsRepo?.let {
                assertEquals(
                    it.balanceTransfer(null), null
                )
            }
        }
    }

    @Test
    fun `test get toll transactions for null`() {
        runTest {
            Mockito.`when`(
                apiService.getTollTransactions(null)
            ).thenReturn(null)
            checkPaidCrossingsRepo?.let {
                assertEquals(
                    it.getTollTransactions(null), null
                )
            }
        }
    }


    @Test
    fun `test get vehicle details for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAccountFindVehicle("",0)
            ).thenReturn(null)
            checkPaidCrossingsRepo?.let {
                assertEquals(
                    it.getVehicleDetail("",0), null
                )
            }
        }
    }

}



