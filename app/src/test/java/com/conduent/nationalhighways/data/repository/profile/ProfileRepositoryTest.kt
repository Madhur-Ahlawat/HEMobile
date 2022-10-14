package com.conduent.nationalhighways.data.repository.profile

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
class ProfileRepositoryTest {

    private var profileRepository: ProfileRepository? = null

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
        profileRepository = ProfileRepository(apiService)
    }

    @Test
    fun `test send email verification api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.sendEmailVerification("", null)
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.emailVerificationApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test get user profile data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getUserProfileData()
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.accountDetail(), null
                )
            }
        }
    }

    @Test
    fun `test email validation for update api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.emailValidationForUpdation(null)
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.emailValidationForUpdation(null), null
                )
            }
        }
    }

    @Test
    fun `test update password api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updatePassword(null)
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.updatePassword(null), null
                )
            }
        }
    }

    @Test
    fun `test update profile data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateProfileData(null)
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.updateProfile(null), null
                )
            }
        }
    }

    @Test
    fun `test update account pin api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateAccountPin(null)
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.updateAccountPin(null), null
                )
            }
        }
    }
    @Test
    fun `test nominated user list api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getNominatedUserList()
            ).thenReturn(null)
            profileRepository?.let {
                assertEquals(
                    it.getNominatedContactList(), null
                )
            }
        }
    }
}