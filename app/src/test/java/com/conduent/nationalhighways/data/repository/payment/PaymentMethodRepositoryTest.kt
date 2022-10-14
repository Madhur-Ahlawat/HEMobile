package com.conduent.nationalhighways.data.repository.payment

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
class NominatedContactsRepoTest {

    private var paymentMethodRepository: PaymentMethodRepository? = null

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
        paymentMethodRepository = PaymentMethodRepository(apiService)
    }

    @Test
    fun `test save card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.savedCard("")
            ).thenReturn(null)
            paymentMethodRepository?.let {
                assertEquals(
                    it.savedCard(), null
                )
            }
        }
    }

    @Test
    fun `test delete card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.deleteCard("",null)
            ).thenReturn(null)
            paymentMethodRepository?.let {
                assertEquals(
                    it.deleteCard(null), null
                )
            }
        }
    }

    @Test
    fun `test edit default card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.editDefaultCard("",null)
            ).thenReturn(null)
            paymentMethodRepository?.let {
                assertEquals(
                    it.editDefaultCard(null), null
                )
            }
        }
    }

    @Test
    fun `test saved card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.savedNewCard("",null)
            ).thenReturn(null)
            paymentMethodRepository?.let {
                assertEquals(
                    it.saveNewCard(null), null
                )
            }
        }
    }

    @Test
    fun `test account details api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.accountDetail()
            ).thenReturn(null)
            paymentMethodRepository?.let {
                assertEquals(
                    it.accountDetail(), null
                )
            }
        }
    }

}