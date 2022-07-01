package com.heandroid.ui.account.creation.step1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.createaccount.ConfirmEmailRequest
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.model.createaccount.EmailVerificationResponse
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class CreateAccountEmailViewModelTest {

    private var createAccountEmailViewModel: CreateAccountEmailViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: CreateAccountRespository

    @Mock
    private lateinit var emailVerificationResponse: Response<EmailVerificationResponse?>

    @Mock
    private lateinit var confirmEmailResponse: Response<EmptyApiResponse?>

    @Inject
    lateinit var errorManager: ErrorManager

    private val unknownException = "unknown exception"

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        createAccountEmailViewModel = CreateAccountEmailViewModel(repository, errorManager)
    }

    @Test
    fun `test email verification api call for success`() {
        runTest {
            Mockito.lenient().`when`(emailVerificationResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(emailVerificationResponse.code()).thenReturn(200)
            val resp = EmailVerificationResponse("", "", "", "")
            val request = EmailVerificationRequest("", "")
            Mockito.lenient().`when`(emailVerificationResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.emailVerificationApiCall(request))
                .thenReturn(emailVerificationResponse)
            createAccountEmailViewModel?.let {
                it.emailVerificationApi(request)
                assertEquals(
                    resp, it.emailVerificationApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test email verification api call for unknown error`() {
        runTest {
            val request = EmailVerificationRequest("", "")
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(emailVerificationResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(emailVerificationResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.emailVerificationApiCall(request))
                .thenReturn(emailVerificationResponse)
            createAccountEmailViewModel?.let {
                it.emailVerificationApi(request)
                assertEquals(
                    null, it.emailVerificationApiVal.value?.data
                )
                assertEquals(
                    message, it.emailVerificationApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test email verification api call for unknown exception`() {
        runTest {
            val request = EmailVerificationRequest("", "")
            Mockito.`when`(repository.emailVerificationApiCall(request))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            createAccountEmailViewModel?.let {
                it.emailVerificationApi(request)
                assertEquals(
                    null, it.emailVerificationApiVal.value?.data
                )
                assertEquals(
                    unknownException, it.emailVerificationApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test confirm email api call for success`() {
        runTest {
            Mockito.lenient().`when`(confirmEmailResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(confirmEmailResponse.code()).thenReturn(200)
            val resp = Mockito.mock(EmptyApiResponse::class.java)
            val request = ConfirmEmailRequest("", "", "")
            Mockito.lenient().`when`(confirmEmailResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.confirmEmailApiCall(request))
                .thenReturn(confirmEmailResponse)
            createAccountEmailViewModel?.let {
                it.confirmEmailApi(request)
                assertEquals(
                    resp, it.confirmEmailApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test confirm email api call for unknown error`() {
        runTest {
            val request = ConfirmEmailRequest("", "", "")
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(confirmEmailResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(confirmEmailResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.confirmEmailApiCall(request))
                .thenReturn(confirmEmailResponse)
            createAccountEmailViewModel?.let {
                it.confirmEmailApi(request)
                assertEquals(
                    null, it.confirmEmailApiVal.value?.data
                )
                assertEquals(
                    message, it.confirmEmailApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test confirm email api call for unknown exception`() {
        runTest {
            val request = ConfirmEmailRequest("", "", "")
            Mockito.`when`(repository.confirmEmailApiCall(request))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            createAccountEmailViewModel?.let {
                it.confirmEmailApi(request)
                assertEquals(
                    null, it.confirmEmailApiVal.value?.data
                )
                assertEquals(
                    unknownException, it.confirmEmailApiVal.value?.errorMsg
                )
            }
        }
    }
}