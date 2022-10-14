package com.conduent.nationalhighways.ui.auth.logout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.auth.login.AuthResponseModel
import com.conduent.nationalhighways.data.repository.auth.LogoutRepository
import com.conduent.nationalhighways.ui.vehicle.TestErrorResponseModel
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
class LogoutViewModelTest {

    private var logoutViewModel: LogoutViewModel? = null


    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: LogoutRepository

    @Mock
    private lateinit var logoutResponse: Response<AuthResponseModel?>

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
        logoutViewModel = LogoutViewModel(repository, errorManager)
    }


    @Test
    fun `test logout api call for success`() {
        runTest {
            Mockito.lenient().`when`(logoutResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(logoutResponse.code()).thenReturn(200)
            val resp = AuthResponseModel("")
            Mockito.lenient().`when`(logoutResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.logout())
                .thenReturn(logoutResponse)
            logoutViewModel?.let {
                it.logout()
                assertEquals(
                    resp, it.logout.value?.data
                )
            }
        }
    }

    @Test
    fun `test logout api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(logoutResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(logoutResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.logout())
                .thenReturn(logoutResponse)
            logoutViewModel?.let {
                it.logout()
                assertEquals(
                    null, it.logout.value?.data
                )
                assertEquals(
                    message, it.logout.value?.errorMsg
                )
            }
        }
    }
}