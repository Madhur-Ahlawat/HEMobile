package com.conduent.nationalhighways.data.repository.viewcharge

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
class ViewChargeRepositoryTest {

    private var viewChargeRepository: ViewChargeRepository? = null

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
        viewChargeRepository = ViewChargeRepository(apiService)
    }

    @Test
    fun `test toll rates api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getTollRates()
            ).thenReturn(null)
            viewChargeRepository?.let {
                assertEquals(
                    it.tollRates(), null
                )
            }
        }
    }
}
