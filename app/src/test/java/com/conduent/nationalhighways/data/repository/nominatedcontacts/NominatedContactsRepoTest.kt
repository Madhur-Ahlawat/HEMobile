package com.conduent.nationalhighways.data.repository.nominatedcontacts

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

    private var nominatedContactsRepo: NominatedContactsRepo? = null

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
        nominatedContactsRepo = NominatedContactsRepo(apiService)
    }

    @Test
    fun `test create secondary account card api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.createSecondaryAccount( null)
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.createSecondaryAccount(null), null
                )
            }
        }
    }
    @Test
    fun `test update access right api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateAccessRight( null)
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.updateAccessRight(null), null
                )
            }
        }
    }
    @Test
    fun `test update secondary account api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateSecondaryAccount( null)
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.updateSecondaryAccount(null), null
                )
            }
        }
    }
    @Test
    fun `test get secondary account api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getSecondaryAccount( )
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.getSecondaryAccount(), null
                )
            }
        }
    }
    @Test
    fun `test get secondary account access rights api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getSecondaryAccessRights( "")
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.getSecondaryAccessRights(""), null
                )
            }
        }
    }
    @Test
    fun `test resend activation mail contacts api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.resendActivationMailContacts( null)
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.resendActivationMailContacts(null), null
                )
            }
        }
    }
    @Test
    fun `test terminate nominated contact api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.terminateNominatedContact( null)
            ).thenReturn(null)
            nominatedContactsRepo?.let {
                assertEquals(
                    it.terminateNominatedContact(null), null
                )
            }
        }
    }

}