package com.heandroid.ui.auth.forgot.password

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.forgot.password.*
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
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
import retrofit2.Response
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@MediumTest
class ForgotPasswordViewModelTest {

    private var forgotPasswordViewModel: ForgotPasswordViewModel? = null

    private val resetPasswordRequest = ResetPasswordModel("", "", "", "", false)
    private val otpRequest = RequestOTPModel("", "")
    private val confirmOptionRequest = ConfirmOptionModel("", "", false)
    private val unknownException = "unknown exception"

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ForgotPasswordRepository

    @Mock
    private lateinit var session: SessionManager

    @Mock
    private lateinit var resetPasswordResponse: Response<ForgotPasswordResponseModel?>

    @Mock
    private lateinit var confirmOptionResponse: Response<ConfirmOptionResponseModel?>

    @Mock
    private lateinit var otpResponse: Response<SecurityCodeResponseModel?>

    @Inject
    lateinit var errorManager: ErrorManager

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        forgotPasswordViewModel = ForgotPasswordViewModel(
            repository,
            errorManager, getInstrumentation().targetContext as Application,
            session
        )
    }


    @Test
    fun `test reset password api call for success`() {
        runTest {
            Mockito.lenient().`when`(resetPasswordResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(resetPasswordResponse.code()).thenReturn(200)
            val resp = ForgotPasswordResponseModel(true, "", "")
            Mockito.lenient().`when`(resetPasswordResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.resetPassword(resetPasswordRequest))
                .thenReturn(resetPasswordResponse)
            forgotPasswordViewModel?.let {
                it.resetPassword(resetPasswordRequest)
                assertEquals(
                    resp, it.resetPassword.value?.data
                )
            }
        }
    }

    @Test
    fun `test reset password api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(resetPasswordResponse.isSuccessful).thenReturn(false)
            val testValidData = TestErrorResponseModel(
                error = message,
                exception = "exception",
                message = message,
                status = status,
                errorCode = status,
                timestamp = ""
            )
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(resetPasswordResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.resetPassword(resetPasswordRequest))
                .thenReturn(resetPasswordResponse)
            forgotPasswordViewModel?.let {
                it.resetPassword(resetPasswordRequest)
                assertEquals(
                    null, it.resetPassword.value?.data
                )
                assertEquals(
                    message, it.resetPassword.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test reset password api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.resetPassword(resetPasswordRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            forgotPasswordViewModel?.let {
                it.resetPassword(resetPasswordRequest)
                assertEquals(
                    null, it.resetPassword.value?.data
                )
                assertEquals(
                    unknownException, it.resetPassword.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test confirm option api call for success`() {
        runTest {
            Mockito.lenient().`when`(confirmOptionResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(confirmOptionResponse.code()).thenReturn(200)
            val resp = ConfirmOptionResponseModel(
                "", "",
                "", "", "",
            )
            Mockito.lenient().`when`(confirmOptionResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.confirmOptionForForgot(confirmOptionRequest))
                .thenReturn(confirmOptionResponse)
            forgotPasswordViewModel?.let {
                it.confirmOptionForForgot(confirmOptionRequest)
                assertEquals(
                    resp, it.confirmOption.value?.data
                )
            }
        }
    }

    @Test
    fun `test confirm option api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(confirmOptionResponse.isSuccessful).thenReturn(false)
            val testValidData = TestErrorResponseModel(
                error = message,
                exception = "exception",
                message = message,
                status = status,
                errorCode = status,
                timestamp = ""
            )
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(confirmOptionResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.confirmOptionForForgot(confirmOptionRequest))
                .thenReturn(confirmOptionResponse)
            forgotPasswordViewModel?.let {
                it.confirmOptionForForgot(confirmOptionRequest)
                assertEquals(
                    null, it.confirmOption.value?.data
                )
                assertEquals(
                    "Network error, could get data,please try again!",
                    it.confirmOption.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test confirm option api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.confirmOptionForForgot(confirmOptionRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            forgotPasswordViewModel?.let {
                it.confirmOptionForForgot(confirmOptionRequest)
                assertEquals(
                    null, it.confirmOption.value?.data
                )
                assertEquals(
                    unknownException, it.confirmOption.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test otp api call for success`() {
        runTest {
            Mockito.lenient().`when`(otpResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(otpResponse.code()).thenReturn(200)
            val resp = SecurityCodeResponseModel("", 0L, "", false)
            Mockito.lenient().`when`(otpResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.requestOTP(otpRequest))
                .thenReturn(otpResponse)
            forgotPasswordViewModel?.let {
                it.requestOTP(otpRequest)
                assertEquals(
                    resp, it.otp.value?.data
                )
            }
        }
    }

    @Test
    fun `test otp api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(otpResponse.isSuccessful).thenReturn(false)
            val testValidData = TestErrorResponseModel(
                error = message,
                exception = "exception",
                message = message,
                status = status,
                errorCode = status,
                timestamp = ""
            )
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(otpResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.requestOTP(otpRequest))
                .thenReturn(otpResponse)
            forgotPasswordViewModel?.let {
                it.requestOTP(otpRequest)
                assertEquals(
                    null, it.otp.value?.data
                )
                assertEquals(
                    message, it.otp.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test otp api api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.requestOTP(otpRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            forgotPasswordViewModel?.let {
                it.requestOTP(otpRequest)
                assertEquals(
                    null, it.otp.value?.data
                )
                assertEquals(
                    unknownException, it.otp.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test password validation`() {
        runTest {
            forgotPasswordViewModel?.let {
                assertEquals(
                    it.checkPassword(
                        ResetPasswordModel(
                            "", "", "password",
                            "password", false
                        )
                    ), Pair(true, "")
                )

                assertEquals(
                    it.checkPassword(
                        ResetPasswordModel(
                            "", "", "password",
                            "passwor", false
                        )
                    ), Pair(false, "Confirm password does not match")
                )
            }
        }
    }
}