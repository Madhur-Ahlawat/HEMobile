package com.heandroid.ui.vehicle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.crossingHistory.*
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.remote.NoConnectivityException
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.utils.common.ConstantsTest
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
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@MediumTest
class VehicleMgmtViewModelTest {

    private val vehicleRequest: VehicleResponse =
        VehicleResponse(PlateInfoResponse(), PlateInfoResponse(), VehicleInfoResponse(), false)
    private val deleteRequest: DeleteVehicleRequest =
        DeleteVehicleRequest("vehicle id")
    private val downloadRequest: TransactionHistoryDownloadRequest =
        TransactionHistoryDownloadRequest()
    private val crossingRequest: CrossingHistoryRequest =
        CrossingHistoryRequest()
    private val successStatus = 200
    private val successMessage = "success"
    private val unknownException = "unknown exception"
    private val connectivityException = "No Internet Connection"
    private var vehicleMgmtViewModel: VehicleMgmtViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: VehicleRepository

    @Mock
    private lateinit var response: Response<EmptyApiResponse?>

    @Mock
    private lateinit var vehicleListResponse: Response<List<VehicleResponse?>?>

    @Mock
    private lateinit var crossingResponse: Response<CrossingHistoryApiResponse?>

    @Mock
    private lateinit var downloadResponse: Response<ResponseBody?>

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
        vehicleMgmtViewModel = VehicleMgmtViewModel(repository, errorManager)
    }

    @Test
    fun `test add vehicle api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body())
                .thenReturn(EmptyApiResponse(successStatus, successMessage))
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    EmptyApiResponse(successStatus, successMessage), it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    successStatus, it.addVehicleApiVal.value?.data?.status
                )
                assertEquals(
                    successMessage, it.addVehicleApiVal.value?.data?.message
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for invalid token error`() {
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
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown error`() {
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
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown error Exception`() {
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
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    "exception", it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown api error model Exception`() {
        runBlockingTest {
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            val testValidData = ""
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(response.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                it.addVehicleApiVal.value?.errorMsg?.contains("java.lang.IllegalStateException", true)
                    ?.let { it1 ->
                        assertTrue(
                            it1
                        )
                    }
            }
        }
    }

    @Test
    fun `test add vehicle api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    connectivityException, it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for timed out exception`() {
        runBlockingTest {
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown exception`() {
        runBlockingTest {
            Mockito.`when`(repository.addVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    unknownException, it.addVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for success`() {
        runBlockingTest {
            val status = 200
            val message = "success"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    response.body(), it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    successStatus, it.updateVehicleApiVal.value?.data?.status
                )
                assertEquals(
                    successMessage, it.updateVehicleApiVal.value?.data?.message
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for invalid token error`() {
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
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.updateVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for unknown error`() {
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
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.updateVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    connectivityException, it.updateVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for timed out exception`() {
        runBlockingTest {
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.updateVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for unknown exception`() {
        runBlockingTest {
            Mockito.`when`(repository.updateVehicleApiCall(vehicleRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    unknownException, it.updateVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for success`() {
        runBlockingTest {
            val status = 200
            val message = "success"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(repository.deleteVehicleListApiCall(deleteRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.deleteVehicleApi(deleteRequest)
                assertEquals(
                    response.body(), it.deleteVehicleApiVal.value?.data
                )
                assertEquals(
                    successStatus, it.deleteVehicleApiVal.value?.data?.status
                )
                assertEquals(
                    successMessage, it.deleteVehicleApiVal.value?.data?.message
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for unknown error`() {
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
            Mockito.`when`(repository.deleteVehicleListApiCall(deleteRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.deleteVehicleApi(deleteRequest)
                assertEquals(
                    null, it.deleteVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.deleteVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.deleteVehicleListApiCall(deleteRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
                it.deleteVehicleApi(deleteRequest)
                assertEquals(
                    null, it.deleteVehicleApiVal.value?.data
                )
                assertEquals(
                    connectivityException, it.deleteVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for timed out exception`() {
        runBlockingTest {
            Mockito.`when`(repository.deleteVehicleListApiCall(deleteRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
                it.deleteVehicleApi(deleteRequest)
                assertEquals(
                    null, it.deleteVehicleApiVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.deleteVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for unknown exception`() {
        runBlockingTest {
            Mockito.`when`(repository.deleteVehicleListApiCall(deleteRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
                it.deleteVehicleApi(deleteRequest)
                assertEquals(
                    null, it.deleteVehicleApiVal.value?.data
                )
                assertEquals(
                    unknownException, it.deleteVehicleApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get vehicle list api call for success`() {
        runBlockingTest {
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
            Mockito.`when`(repository.getVehicleListApiCall()).thenReturn(vehicleListResponse)
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleListResponse.code()).thenReturn(200)
            val vehicleList = listOf<VehicleResponse>()
            Mockito.lenient().`when`(vehicleListResponse.body()).thenReturn(vehicleList)
            Mockito.`when`(repository.getVehicleListApiCall()).thenReturn(vehicleListResponse)
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
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
            Mockito.`when`(repository.getVehicleListApiCall()).thenReturn(vehicleListResponse)
            vehicleMgmtViewModel?.let {
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
    fun `test get vehicle list api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.getVehicleListApiCall())
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.`when`(repository.getVehicleListApiCall())
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.`when`(repository.getVehicleListApiCall())
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
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
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.lenient().`when`(crossingResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(crossingResponse.code()).thenReturn(200)
            val crossingList = mutableListOf<CrossingHistoryItem?>()
            val crossingHistoryResponseData = CrossingHistoryResponse(crossingList, "")
            val crossingHistoryResponse =
                CrossingHistoryApiResponse(crossingHistoryResponseData, "", "")
            Mockito.lenient().`when`(crossingResponse.body()).thenReturn(crossingHistoryResponse)
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenReturn(crossingResponse)
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
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
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
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
        runBlockingTest {
            Mockito.`when`(repository.crossingHistoryApiCall(crossingRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
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
    fun `test download vehicle crossing history api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(downloadResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(downloadResponse.code()).thenReturn(200)
            Mockito.lenient().`when`(downloadResponse.body()).thenReturn(responseBody)
            Mockito.`when`(repository.downloadCrossingHistoryAPiCall(downloadRequest))
                .thenReturn(downloadResponse)
            vehicleMgmtViewModel?.let {
                it.downloadCrossingHistoryApiCall(downloadRequest)
                assertEquals(
                    downloadResponse.body(), it.crossingHistoryDownloadVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test download vehicle crossing history api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(downloadResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(downloadResponse.errorBody()).thenReturn(responseBody)

            Mockito.`when`(repository.downloadCrossingHistoryAPiCall(downloadRequest))
                .thenReturn(downloadResponse)
            vehicleMgmtViewModel?.let {
                it.downloadCrossingHistoryApiCall(downloadRequest)
                assertEquals(
                    null, it.crossingHistoryDownloadVal.value?.data
                )
                assertEquals(
                    message, it.crossingHistoryDownloadVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test download vehicle crossing history api call for no internet connection`() {
        runBlockingTest {
            Mockito.`when`(repository.downloadCrossingHistoryAPiCall(downloadRequest))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleMgmtViewModel?.let {
                it.downloadCrossingHistoryApiCall(downloadRequest)
                assertEquals(
                    null, it.crossingHistoryDownloadVal.value?.data
                )
                assertEquals(
                    connectivityException, it.crossingHistoryDownloadVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test download vehicle crossing history api call for timed out exception`() {
        runBlockingTest {
            Mockito.`when`(repository.downloadCrossingHistoryAPiCall(downloadRequest))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleMgmtViewModel?.let {
                it.downloadCrossingHistoryApiCall(downloadRequest)
                assertEquals(
                    null, it.crossingHistoryDownloadVal.value?.data
                )
                assertEquals(
                    ConstantsTest.VPN_ERROR, it.crossingHistoryDownloadVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test download vehicle crossing history api call for unknown exception`() {
        runBlockingTest {
            Mockito.`when`(repository.downloadCrossingHistoryAPiCall(downloadRequest))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleMgmtViewModel?.let {
                it.downloadCrossingHistoryApiCall(downloadRequest)
                assertEquals(
                    null, it.crossingHistoryDownloadVal.value?.data
                )
                assertEquals(
                    unknownException, it.crossingHistoryDownloadVal.value?.errorMsg
                )
            }
        }
    }
}

data class TestErrorResponseModel(
    val error: String?,
    val exception: String?,
    @SerializedName("error_description", alternate = ["message"])
    val message: String?,
    val status: Int?,
    val errorCode: Int?,
    val timestamp: String?
)