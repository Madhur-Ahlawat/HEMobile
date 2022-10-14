package com.conduent.nationalhighways.ui.bottomnav.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.LargeTest
import com.google.gson.Gson
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.*
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryApiResponse
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryItem
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryRequest
import com.conduent.nationalhighways.data.model.crossingHistory.CrossingHistoryResponse
import com.conduent.nationalhighways.data.model.notification.AlertMessageApiResponse
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.data.remote.NoConnectivityException
import com.conduent.nationalhighways.data.repository.dashboard.DashBoardRepo
import com.conduent.nationalhighways.ui.vehicle.TestErrorResponseModel
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.data.DataFile
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
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@LargeTest
class DashboardViewModelTest {

    private var dashboardViewModel: DashboardViewModel? = null

    private val crossingRequest: CrossingHistoryRequest =
        CrossingHistoryRequest()

    private val unknownException = "unknown exception"
    private val connectivityException = "No Internet Connection found"

    @Mock
    private lateinit var vehicleListResponse: Response<List<VehicleResponse?>?>

    @Mock
    private lateinit var accountResponse: Response<AccountResponse?>

    @Mock
    private lateinit var alertResponse: Response<AlertMessageApiResponse?>

    @Mock
    private lateinit var thresholdResponse: Response<ThresholdAmountApiResponse?>

    @Mock
    private lateinit var crossingResponse: Response<CrossingHistoryApiResponse?>

    @Mock
    private lateinit var repository: DashBoardRepo

    @Inject
    lateinit var errorManager: ErrorManager

    @Mock
    private lateinit var responseBody: ResponseBody

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        dashboardViewModel = DashboardViewModel(repository, errorManager)
    }

    @Test
    fun `test get vehicle list api call for success`() {
        runTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleListResponse.code()).thenReturn(200)
            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234"),
                VehicleInfoResponse(),
                false
            )
            val v2 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("ABCD"),
                VehicleInfoResponse(),
                false
            )
            val vehicleList = listOf(v1, v2)
            Mockito.lenient().`when`(vehicleListResponse.body()).thenReturn(vehicleList)
            Mockito.`when`(repository.getVehicleData()).thenReturn(vehicleListResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    vehicleList.size, it.vehicleListVal.value?.data?.size
                )
                assertEquals(
                    vehicleListResponse.body(), it.vehicleListVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for success with no vehicles`() {
        runTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleListResponse.code()).thenReturn(200)
            val vehicleList = listOf<VehicleResponse>()
            Mockito.lenient().`when`(vehicleListResponse.body()).thenReturn(vehicleList)
            Mockito.`when`(repository.getVehicleData()).thenReturn(vehicleListResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    vehicleList.size, it.vehicleListVal.value?.data?.size
                )
                assertEquals(
                    vehicleListResponse.body(), it.vehicleListVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(vehicleListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleData()).thenReturn(vehicleListResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    message, it.vehicleListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for unknown error Exception`() {
        runTest {
            val status = 403
            val message = ""
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(vehicleListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleData()).thenReturn(vehicleListResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    "exception", it.vehicleListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for unknown error model Exception`() {
        runTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(false)
            val testValidData = ""
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(vehicleListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleData()).thenReturn(vehicleListResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                it.vehicleListVal.value?.errorMsg?.contains("java.lang.IllegalStateException", true)
                    ?.let { it1 ->
                        assertTrue(
                            it1
                        )
                    }
            }
        }
    }

    @Test
    fun `test get vehicle list api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.getVehicleData())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    connectivityException, it.vehicleListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.getVehicleData())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.vehicleListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getVehicleData())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    unknownException, it.vehicleListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for success`() {
        runTest {
            Mockito.lenient().`when`(crossingResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(crossingResponse.code()).thenReturn(200)
            val item1 = DataFile.getCrossingHistoryItem("1234")
            val item2 = DataFile.getCrossingHistoryItem("ABCD")
            val crossingList = mutableListOf(item1, item2)
            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")
            Mockito.lenient().`when`(crossingResponse.body()).thenReturn(crossingHistoryResponse)
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenReturn(crossingResponse)
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    crossingResponse.body()?.transactionList?.transaction?.size,
                    it.crossingHistoryVal.value?.data?.transactionList?.transaction?.size
                )
                assertEquals(
                    crossingList, it.crossingHistoryVal.value?.data?.transactionList?.transaction
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for success with no crossings`() {
        runTest {
            Mockito.lenient().`when`(crossingResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(crossingResponse.code()).thenReturn(200)
            val crossingList = mutableListOf<CrossingHistoryItem?>()
            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")
            Mockito.lenient().`when`(crossingResponse.body()).thenReturn(crossingHistoryResponse)
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenReturn(crossingResponse)
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    0, it.crossingHistoryVal.value?.data?.transactionList?.transaction?.size
                )
                assertEquals(
                    crossingList, it.crossingHistoryVal.value?.data?.transactionList?.transaction
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(crossingResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(crossingResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenReturn(crossingResponse)
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    null, it.crossingHistoryVal.value?.data
                )
                assertEquals(
                    message, it.crossingHistoryVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    null, it.crossingHistoryVal.value?.data
                )
                assertEquals(
                    connectivityException, it.crossingHistoryVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    null, it.crossingHistoryVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.crossingHistoryVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test vehicle crossing history api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            dashboardViewModel?.let {
                it.crossingHistoryApiCall(crossingRequest)
                assertEquals(
                    null, it.crossingHistoryVal.value?.data
                )
                assertEquals(
                    unknownException, it.crossingHistoryVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test account overview api call for success`() {
        runTest {
            val response = DataFile.getAccountResponse()
            Mockito.lenient().`when`(accountResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(accountResponse.code()).thenReturn(200)
            Mockito.lenient().`when`(accountResponse.body()).thenReturn(response)

            Mockito.`when`(repository.getAccountDetailsApiCall()).thenReturn(accountResponse)
            dashboardViewModel?.let {
                it.getAccountDetailsData()
                assertEquals(
                    response, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    accountResponse.body(), it.accountOverviewVal.value?.data
                )
            }
        }
    }


    @Test
    fun `test account overview api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(accountResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(accountResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getAccountDetailsApiCall()).thenReturn(accountResponse)
            dashboardViewModel?.let {
                it.getAccountDetailsData()
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    message, it.accountOverviewVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test account overview api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.getAccountDetailsApiCall())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            dashboardViewModel?.let {
                it.getAccountDetailsData()
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    connectivityException, it.accountOverviewVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test account overview api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.getAccountDetailsApiCall())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            dashboardViewModel?.let {
                it.getAccountDetailsData()
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.accountOverviewVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test account overview api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getAccountDetailsApiCall())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            dashboardViewModel?.let {
                it.getAccountDetailsData()
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    unknownException, it.accountOverviewVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get alerts api call for success`() {
        runTest {
            val response = AlertMessageApiResponse(0, "", null)
            Mockito.lenient().`when`(alertResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(alertResponse.code()).thenReturn(200)
            Mockito.lenient().`when`(alertResponse.body()).thenReturn(response)
            Mockito.`when`(repository.getAlertMessages()).thenReturn(alertResponse)
            dashboardViewModel?.let {
                it.getAlertsApi()
                assertEquals(
                    response, it.getAlertsVal.value?.data
                )
                assertEquals(
                    alertResponse.body(), it.getAlertsVal.value?.data
                )
            }
        }
    }


    @Test
    fun `test get alerts api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(alertResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(alertResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getAlertMessages()).thenReturn(alertResponse)
            dashboardViewModel?.let {
                it.getAlertsApi()
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    message, it.getAlertsVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get alerts api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.getAlertMessages())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            dashboardViewModel?.let {
                it.getAlertsApi()
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    connectivityException, it.getAlertsVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get alerts api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.getAlertMessages())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            dashboardViewModel?.let {
                it.getAlertsApi()
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.getAlertsVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get alerts api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getAlertMessages())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            dashboardViewModel?.let {
                it.getAlertsApi()
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    unknownException, it.getAlertsVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get threshold amount data api call for success`() {
        runTest {
            val response = ThresholdAmountApiResponse(ThresholdAmountData("", ""), "", "")
            Mockito.lenient().`when`(thresholdResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(thresholdResponse.code()).thenReturn(200)
            Mockito.lenient().`when`(thresholdResponse.body()).thenReturn(response)
            Mockito.`when`(repository.getThresholdAmountApiCAll()).thenReturn(thresholdResponse)
            dashboardViewModel?.let {
                it.getThresholdAmountData()
                assertEquals(
                    response, it.thresholdAmountVal.value?.data
                )
                assertEquals(
                    thresholdResponse.body(), it.thresholdAmountVal.value?.data
                )
            }
        }
    }


    @Test
    fun `test get threshold amount data api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(thresholdResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(thresholdResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getThresholdAmountApiCAll()).thenReturn(thresholdResponse)
            dashboardViewModel?.let {
                it.getThresholdAmountData()
                assertEquals(
                    null, it.thresholdAmountVal.value?.data
                )
                assertEquals(
                    message, it.thresholdAmountVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get threshold amount data api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.getThresholdAmountApiCAll())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            dashboardViewModel?.let {
                it.getThresholdAmountData()
                assertEquals(
                    null, it.thresholdAmountVal.value?.data
                )
                assertEquals(
                    connectivityException, it.thresholdAmountVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get threshold amount data api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.getThresholdAmountApiCAll())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            dashboardViewModel?.let {
                it.getThresholdAmountData()
                assertEquals(
                    null, it.thresholdAmountVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.thresholdAmountVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get threshold amount data api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getThresholdAmountApiCAll())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            dashboardViewModel?.let {
                it.getThresholdAmountData()
                assertEquals(
                    null, it.thresholdAmountVal.value?.data
                )
                assertEquals(
                    unknownException, it.thresholdAmountVal.value?.errorMsg
                )
            }
        }
    }

}