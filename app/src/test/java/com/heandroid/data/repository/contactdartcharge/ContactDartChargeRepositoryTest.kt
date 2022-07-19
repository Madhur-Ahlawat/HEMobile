package com.heandroid.data.repository.contactdartcharge

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
class ContactDartChargeRepositoryTest {

    private var contactDartChargeRepository: ContactDartChargeRepository? = null

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
        contactDartChargeRepository = ContactDartChargeRepository(apiService)
    }

    @Test
    fun `test get case history data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getCaseHistoryData(null)
            ).thenReturn(null)
            contactDartChargeRepository?.let {
                assertEquals(
                    it.getCaseHistoryDataApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test get case category list for null`() {
        runTest {
            Mockito.`when`(
                apiService.getCaseCategoriesList()
            ).thenReturn(null)
            contactDartChargeRepository?.let {
                assertEquals(
                    it.getCaseCategoriesList(), null
                )
            }
        }
    }

    @Test
    fun `test get case sub category list for null`() {
        runTest {
            Mockito.`when`(
                apiService.getCaseSubCategoriesList("")
            ).thenReturn(null)
            contactDartChargeRepository?.let {
                assertEquals(
                    it.getCaseSubCategoriesList(""), null
                )
            }
        }
    }
    @Test
    fun `test create new case for null`() {
        runTest {
            Mockito.`when`(
                apiService.createNewCase(null)
            ).thenReturn(null)
            contactDartChargeRepository?.let {
                assertEquals(
                    it.createNewCase(null), null
                )
            }
        }
    }
    @Test
    fun `test upload File for null`() {
        runTest {
            Mockito.`when`(
                apiService.uploadFile(null)
            ).thenReturn(null)
            contactDartChargeRepository?.let {
                assertEquals(
                    it.uploadFile(null), null
                )
            }
        }
    }

}