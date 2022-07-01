package com.heandroid.ui.auth.forgot.email

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.forgot.email.ForgotEmailModel
import com.heandroid.data.model.auth.forgot.email.ForgotEmailResponseModel
import com.heandroid.data.repository.auth.ForgotEmailRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
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
class ForgotEmailViewModelTest {

    private var forgotEmailViewModel: ForgotEmailViewModel? = null

    private val forgotEmailRequest = ForgotEmailModel(false, "", "")
    private val unknownException = "unknown exception"

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ForgotEmailRepository

    @Mock
    private lateinit var forgotEmailResponse: Response<ForgotEmailResponseModel?>

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
        forgotEmailViewModel = ForgotEmailViewModel(
            repository, errorManager,
        )
    }


    @Test
    fun `test forgot email api call for success`() {
        runTest {
            Mockito.lenient().`when`(forgotEmailResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(forgotEmailResponse.code()).thenReturn(200)
            val resp = ForgotEmailResponseModel("")
            Mockito.lenient().`when`(forgotEmailResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.forgotEmail(forgotEmailRequest))
                .thenReturn(forgotEmailResponse)
            forgotEmailViewModel?.let {
                it.forgotEmail(forgotEmailRequest)
                assertEquals(
                    resp, it.forgotEmail.value?.data
                )
            }
        }
    }

    @Test
    fun `test forgot email api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(forgotEmailResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(forgotEmailResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.forgotEmail(forgotEmailRequest))
                .thenReturn(forgotEmailResponse)
            forgotEmailViewModel?.let {
                it.forgotEmail(forgotEmailRequest)
                assertEquals(
                    null, it.forgotEmail.value?.data
                )
                assertEquals(
                    message, it.forgotEmail.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test forgot email api call for unknown error Exception`() {
        runTest {
            val status = 403
            val message = ""
            Mockito.lenient().`when`(forgotEmailResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(forgotEmailResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.forgotEmail(forgotEmailRequest)).thenReturn(forgotEmailResponse)
            forgotEmailViewModel?.let {
                it.forgotEmail(forgotEmailRequest)
                assertEquals(
                    null, it.forgotEmail.value?.data
                )
                assertEquals(
                    "exception", it.forgotEmail.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test forgot email api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.forgotEmail(forgotEmailRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            forgotEmailViewModel?.let {
                it.forgotEmail(forgotEmailRequest)
                assertEquals(
                    null, it.forgotEmail.value?.data
                )
                assertEquals(
                    unknownException, it.forgotEmail.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test masking user name`() {
        runTest {
            val userName = "developer"
            val ans = "de*****er"
            forgotEmailViewModel?.let {
                assertEquals(
                    it.loadUserName(userName).toString(), ans
                )
            }
        }
    }
}