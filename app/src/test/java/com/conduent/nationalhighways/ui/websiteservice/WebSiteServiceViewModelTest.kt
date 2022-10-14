package com.conduent.nationalhighways.ui.websiteservice

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.data.remote.NoConnectivityException
import com.conduent.nationalhighways.data.repository.websiteservice.WebsiteServiceRepository
import com.conduent.nationalhighways.ui.vehicle.TestErrorResponseModel
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class WebSiteServiceViewModelTest {
    private val webSiteStatus: WebSiteStatus =
        WebSiteStatus("", "", "", "", "", "")

    private val unknownException = "unknown exception"
    private val connectivityException = "No Internet Connection found"

    private var webSiteServiceViewModel: WebSiteServiceViewModel? = null

    @Mock
    private lateinit var repository: WebsiteServiceRepository

    @Mock
    private lateinit var response: Response<WebSiteStatus?>

    @Mock
    private lateinit var manager: SessionManager

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
        webSiteServiceViewModel = WebSiteServiceViewModel(repository, errorManager)
    }

    @Test
    fun `test toll rates api call for success`() {
        runTest {
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body())
                .thenReturn(webSiteStatus)
            Mockito.`when`(repository.webSiteServiceStatus()).thenReturn(response)
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    webSiteStatus, it.webServiceLiveData.value?.data
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for invalid token error`() {
        runTest {
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
            Mockito.`when`(repository.webSiteServiceStatus()).thenReturn(response)
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    message, it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown error`() {
        runTest {
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
            Mockito.`when`(repository.webSiteServiceStatus()).thenReturn(response)
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    message, it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown error Exception`() {
        runTest {
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
            Mockito.`when`(repository.webSiteServiceStatus()).thenReturn(response)
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    "exception", it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown api error model Exception`() {
        runTest {
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            val testValidData = ""
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.webSiteServiceStatus()).thenReturn(response)
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                it.webServiceLiveData.value?.errorMsg?.contains(
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
        runTest {
            Mockito.`when`(repository.webSiteServiceStatus())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    connectivityException, it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.webSiteServiceStatus())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    ConstantsTest.VPN_ERROR, it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test toll rates api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.webSiteServiceStatus())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            webSiteServiceViewModel?.let {
                it.checkServiceStatus()
                Assert.assertEquals(
                    null, it.webServiceLiveData.value?.data
                )
                Assert.assertEquals(
                    unknownException, it.webServiceLiveData.value?.errorMsg
                )
            }
        }
    }
}