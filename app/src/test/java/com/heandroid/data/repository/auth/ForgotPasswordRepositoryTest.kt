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
class ForgotPasswordRepositoryTest {

    private var forgotPasswordRepository: ForgotPasswordRepository? = null

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
        forgotPasswordRepository = ForgotPasswordRepository(apiService)
    }

    @Test
    fun `test conform options for forgot for null`() {
        runTest {
            Mockito.`when`(
                apiService.confirmOptionForForgot("", null)
            ).thenReturn(null)
            forgotPasswordRepository?.let {
                assertEquals(
                    it.confirmOptionForForgot(null), null
                )
            }
        }
    }

    @Test
    fun `test request otp for null`() {
        runTest {
            Mockito.`when`(apiService.requestOTP("", null)).thenReturn(null)
            forgotPasswordRepository?.let {
                assertEquals(it.requestOTP(null), null)
            }
        }
    }

    @Test
    fun `test reset password for null`() {
        runTest {
            Mockito.`when`(apiService.resetPassword("", null)).thenReturn(null)
            forgotPasswordRepository?.let {
                assertEquals(it.resetPassword(null), null)
            }
        }
    }
}