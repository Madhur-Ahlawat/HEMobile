package com.heandroid.data.repository.makeoneofpayments

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
class MakeOneOfPaymentRepoTest {

    private var makeOneOfPaymentRepo: MakeOneOfPaymentRepo? = null

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
        makeOneOfPaymentRepo = MakeOneOfPaymentRepo(apiService)
    }

    @Test
    fun `test get crossing details data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getCrossingDetails(null)
            ).thenReturn(null)
            makeOneOfPaymentRepo?.let {
                assertEquals(
                    it.getCrossingDetails(null), null
                )
            }
        }
    }
    @Test
    fun `test where to receive payments receipts pay api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.whereToReceivePaymentReceipt(null)
            ).thenReturn(null)
            makeOneOfPaymentRepo?.let {
                assertEquals(
                    it.whereToReceivePaymentReceipt(null), null
                )
            }
        }
    }

    @Test
    fun `test get one of payments pay api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.oneOfPaymentsPay(null)
            ).thenReturn(null)
            makeOneOfPaymentRepo?.let {
                assertEquals(
                    it.oneOfPaymentsPay(null), null
                )
            }
        }
    }


}