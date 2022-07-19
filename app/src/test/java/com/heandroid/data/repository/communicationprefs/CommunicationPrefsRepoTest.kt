package com.heandroid.data.repository.communicationprefs


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
class CommunicationPrefsRepoTest {

    private var communicationPrefsRepo: CommunicationPrefsRepo? = null

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
        communicationPrefsRepo = CommunicationPrefsRepo(apiService)
    }

    @Test
    fun `test get account settings for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAccountSettings()
            ).thenReturn(null)
            communicationPrefsRepo?.let {
                assertEquals(
                    it.getAccountSettingsPrefs(), null
                )
            }
        }
    }

    @Test
    fun `test update communication settings prefs for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateCommunicationPrefs(null)
            ).thenReturn(null)
            communicationPrefsRepo?.let {
                assertEquals(
                    it.updateCommunicationSettingsPrefs(null), null
                )
            }
        }
    }

    @Test
    fun `test update account setting prefs for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateAccountSettingPrefs(null)
            ).thenReturn(null)
            communicationPrefsRepo?.let {
                assertEquals(
                    it.updateAccountSettingPrefs(null), null
                )
            }
        }
    }

    @Test
    fun `test search process parameters for null`() {
        runTest {
            Mockito.`when`(
                apiService.searchProcessParameters(null)
            ).thenReturn(null)
            communicationPrefsRepo?.let {
                assertEquals(
                    it.searchProcessParameters(null), null
                )
            }
        }
    }


}