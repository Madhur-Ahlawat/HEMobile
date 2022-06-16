package com.heandroid.ui.auth.forgot.password

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.forgot.password.ForgotPasswordResponseModel
import com.heandroid.data.model.auth.forgot.password.ResetPasswordModel
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Assert.*
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

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ForgotPasswordRepository

    @Mock
    private lateinit var resetPasswordResponse: Response<ForgotPasswordResponseModel?>

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
        forgotPasswordViewModel = ForgotPasswordViewModel(repository, errorManager)
    }


    @Test
    fun `test reset password api call for success`() {
        runBlockingTest {
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
        runBlockingTest {
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
}