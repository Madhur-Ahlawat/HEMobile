package com.conduent.nationalhighways.data.repository.manualtopup

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
class ManualTopUpRepositoryTest {

    private var manualTopUpRepository: ManualTopUpRepository? = null

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
        manualTopUpRepository = ManualTopUpRepository(apiService)
    }

    @Test
    fun `test payment with new card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.paymentWithNewCard("",null)
            ).thenReturn(null)
            manualTopUpRepository?.let {
                assertEquals(
                    it.paymentWithNewCard(null), null
                )
            }
        }
    }

    @Test
    fun `test payment with existing card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.paymentWithExistingCard("",null)
            ).thenReturn(null)
            manualTopUpRepository?.let {
                assertEquals(
                    it.paymentWithExistingCard(null), null
                )
            }
        }
    }
}