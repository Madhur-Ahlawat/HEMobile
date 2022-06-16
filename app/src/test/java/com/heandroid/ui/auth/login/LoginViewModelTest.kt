package com.heandroid.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.data.DataFile
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
class LoginViewModelTest {

    private var loginViewModel: LoginViewModel? = null

    private val loginRequest = LoginModel("", "")

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: LoginRepository

    @Mock
    private lateinit var loginResponse: Response<LoginResponse?>

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
        loginViewModel = LoginViewModel(repository, errorManager)
    }


    @Test
    fun `test login api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(loginResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(loginResponse.code()).thenReturn(200)
            val resp = DataFile.getLoginResponse()
            Mockito.lenient().`when`(loginResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.login(loginRequest))
                .thenReturn(loginResponse)
            loginViewModel?.let {
                it.login(loginRequest)
                assertEquals(
                    resp, it.login.value?.data
                )
            }
        }
    }

    @Test
    fun `test login api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(loginResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(loginResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.login(loginRequest))
                .thenReturn(loginResponse)
            loginViewModel?.let {
                it.login(loginRequest)
                assertEquals(
                    null, it.login.value?.data
                )
                assertEquals(
                    message, it.login.value?.errorMsg
                )
            }
        }
    }
}