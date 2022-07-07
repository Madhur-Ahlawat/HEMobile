package com.heandroid.ui.viewcharges

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.tollrates.TollRatesResp
import com.heandroid.data.remote.NoConnectivityException
import com.heandroid.data.repository.viewcharge.ViewChargeRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.common.ConstantsTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Assert
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
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@MediumTest
class ViewChargeViewModelTest {

    private val unknownException = "unknown exception"
    private val connectivityException = "No Internet Connection"
    private var viewChargeViewModel: ViewChargeViewModel? = null

    @Mock
    private lateinit var repository: ViewChargeRepository

    @Mock
    private lateinit var response: Response<List<TollRatesResp?>?>

    @Mock
    private lateinit var responseBody: ResponseBody

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
        viewChargeViewModel = ViewChargeViewModel(repository, errorManager)
    }

    @Test
    fun `test toll rates api call for success`() {
        runBlockingTest {
            val resp1 = TollRatesResp(0, "", 0.0, 0.0)
            val resp2 = TollRatesResp(0, "", 0.0, 0.0)
            val list = listOf(resp1, resp2)
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body())
                .thenReturn(list)
            Mockito.`when`(repository.tollRates()).thenReturn(response)
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    list, it.tollRates.value?.data
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for invalid token error`() {
        runBlockingTest {
            val status = 401
            val message = "Invalid token"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.tollRates()).thenReturn(response)
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    message, it.tollRates.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.tollRates()).thenReturn(response)
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    message, it.tollRates.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown error Exception`() {
        runBlockingTest {
            val status = 403
            val message = ""
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.tollRates()).thenReturn(response)
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    "exception", it.tollRates.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown api error model Exception`() {
        runBlockingTest {
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            val testValidData = ""
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.tollRates()).thenReturn(response)
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                it.tollRates.value?.errorMsg?.contains(
                    "java.lang.IllegalStateException",
                    true
                )
                    ?.let { it1 ->
                        Assert.assertTrue(
                            it1
                        )
                    }
            }
        }
    }

    @Test
    fun `test toll rates api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.tollRates())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    connectivityException, it.tollRates.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for timed out exception`() {
        runBlockingTest {
            Mockito.`when`(repository.tollRates())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    ConstantsTest.VPN_ERROR, it.tollRates.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown exception`() {
        runBlockingTest {
            Mockito.`when`(repository.tollRates())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            viewChargeViewModel?.let {
                it.tollRates()
                Assert.assertEquals(
                    null, it.tollRates.value?.data
                )
                Assert.assertEquals(
                    unknownException, it.tollRates.value?.errorMsg
                )
            }
        }
    }
}