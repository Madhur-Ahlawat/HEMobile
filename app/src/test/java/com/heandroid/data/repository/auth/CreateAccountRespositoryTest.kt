package com.heandroid.data.repository.auth

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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class CreateAccountRespositoryTest {

    private var createAccountRespository: CreateAccountRespository? = null

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
        createAccountRespository = CreateAccountRespository(apiService)
    }

    @Test
    fun `test create account for null`() {
        runTest {
            Mockito.`when`(
                apiService.createAccount("", null)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.createAccount(null), null
                )
            }
        }
    }

    @Test
    fun `test email verification for null`() {
        runTest {
            Mockito.`when`(
                apiService.sendEmailVerification("", null)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.emailVerificationApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test confirm email for null`() {
        runTest {
            Mockito.`when`(
                apiService.confirmEmailVerification("", null)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.confirmEmailApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test vehicle details for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAccountFindVehicle("", 0)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.getVehicleDetail("", 0), null
                )
            }
        }
    }

    @Test
    fun `test valid vehicle check for null`() {
        runTest {
            Mockito.`when`(
                apiService.validVehicleCheck(null, 0)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.validVehicleCheck(null, 0), null
                )
            }
        }
    }

    @Test
    fun `test user name availability check for null`() {
        runTest {
            Mockito.`when`(
                apiService.userNameAvailabilityCheck(null)
            ).thenReturn(null)
            createAccountRespository?.let {
                assertEquals(
                    it.userNameAvailabilityCheck(null), null
                )
            }
        }
    }

}