package com.heandroid.data.repository.websiteservice

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
class WebsiteServiceRepositoryTest {

    private var websiteServiceRepository: WebsiteServiceRepository? = null

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
        websiteServiceRepository = WebsiteServiceRepository(apiService)
    }

    @Test
    fun `test web site service status api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.webSiteServiceStatus()
            ).thenReturn(null)
            websiteServiceRepository?.let {
                assertEquals(
                    it.webSiteServiceStatus(), null
                )
            }
        }
    }
}
