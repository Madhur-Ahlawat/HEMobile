package com.heandroid.ui.vehicle.vehiclegroup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.vehicle.*
import com.heandroid.data.remote.NoConnectivityException
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.common.ConstantsTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
@MediumTest
class VehicleGroupMgmtViewModelTest {

    private val groupVehicleRequest: VehicleGroupResponse =
        VehicleGroupResponse("", "", "")

    private val addDeleteVehicleGroup: AddDeleteVehicleGroup =
        AddDeleteVehicleGroup("")

    private val renameVehicleGroup: RenameVehicleGroup =
        RenameVehicleGroup("", "")

    private val unknownException = "unknown exception"
    private val connectivityException = "No Internet Connection"
    private var vehicleGroupMgmtViewModel: VehicleGroupMgmtViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: VehicleRepository

    @Mock
    private lateinit var vehicleListResponse: Response<List<VehicleResponse?>?>

    @Mock
    private lateinit var vehicleGroupListResponse: Response<List<VehicleGroupResponse?>?>

    @Mock
    private lateinit var vehicleGroupMngmtResponse: Response<VehicleGroupMngmtResponse?>

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
        vehicleGroupMgmtViewModel = VehicleGroupMgmtViewModel(repository, errorManager)
    }

    @Test
    fun `test get vehicle list of vehicle group api call for success`() {
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
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for success with no vehicles`() {
        runTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleListResponse.code()).thenReturn(200)
            val vehicleList = listOf<VehicleResponse>()
            Mockito.lenient().`when`(vehicleListResponse.body()).thenReturn(vehicleList)
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for unknown error`() {
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
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for no internet connection`() {
        runTest {
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenAnswer {
                    throw NoConnectivityException()
                }
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for timed out exception`() {
        runTest {
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenAnswer {
                    throw SocketTimeoutException()
                }
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for unknown error Exception`() {
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
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle list of vehicle group api call for unknown api error model Exception`() {
        runTest {
            Mockito.lenient().`when`(vehicleListResponse.isSuccessful).thenReturn(false)
            val testValidData = ""
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(vehicleListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleListOfGroupApiCall(""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehiclesOfGroupApi(groupVehicleRequest)
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
    fun `test get vehicle groups list api call for success`() {
        runTest {
            Mockito.lenient().`when`(vehicleGroupListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleGroupListResponse.code()).thenReturn(200)
            val v1 = VehicleGroupResponse("", "", "")
            val v2 = VehicleGroupResponse("", "", "")
            val vehicleGroupList = listOf(v1, v2)
            Mockito.lenient().`when`(vehicleGroupListResponse.body()).thenReturn(vehicleGroupList)
            Mockito.`when`(repository.getVehicleGroupListApiCall())
                .thenReturn(vehicleGroupListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehicleGroupListApi()
                assertEquals(
                    vehicleGroupList.size, it.getVehicleGroupListApiVal.value?.data?.size
                )
                assertEquals(
                    vehicleGroupListResponse.body(), it.getVehicleGroupListApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get vehicle group list api call for success with no vehicle groups`() {
        runTest {
            Mockito.lenient().`when`(vehicleGroupListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleGroupListResponse.code()).thenReturn(200)
            val vehicleList = listOf<VehicleGroupResponse>()
            Mockito.lenient().`when`(vehicleGroupListResponse.body()).thenReturn(vehicleList)
            Mockito.`when`(repository.getVehicleGroupListApiCall())
                .thenReturn(vehicleGroupListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehicleGroupListApi()
                assertEquals(
                    vehicleList.size, it.getVehicleGroupListApiVal.value?.data?.size
                )
                assertEquals(
                    vehicleList, it.getVehicleGroupListApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get vehicle group list api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(vehicleGroupListResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(vehicleGroupListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleGroupListApiCall())
                .thenReturn(vehicleGroupListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getVehicleGroupListApi()
                assertEquals(
                    null, it.getVehicleGroupListApiVal.value?.data
                )
                assertEquals(
                    message, it.getVehicleGroupListApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test add vehicle group api call for success`() {
        runTest {
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.code()).thenReturn(200)
            val response = VehicleGroupMngmtResponse(false, "", "")
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.body()).thenReturn(response)
            Mockito.`when`(repository.addVehicleGroupApiCall(addDeleteVehicleGroup))
                .thenReturn(vehicleGroupMngmtResponse)
            vehicleGroupMgmtViewModel?.let {
                it.addVehicleGroupApi(addDeleteVehicleGroup)
                assertEquals(
                    response, it.addVehicleGroupApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test add vehicle group api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.addVehicleGroupApiCall(addDeleteVehicleGroup))
                .thenReturn(vehicleGroupMngmtResponse)
            vehicleGroupMgmtViewModel?.let {
                it.addVehicleGroupApi(addDeleteVehicleGroup)
                assertEquals(
                    null, it.addVehicleGroupApiVal.value?.data
                )
                assertEquals(
                    message, it.addVehicleGroupApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test rename vehicle group api call for success`() {
        runTest {
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.code()).thenReturn(200)
            val response = VehicleGroupMngmtResponse(false, "", "")
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.body()).thenReturn(response)
            Mockito.`when`(repository.renameVehicleGroupApiCall(renameVehicleGroup))
                .thenReturn(vehicleGroupMngmtResponse)
            vehicleGroupMgmtViewModel?.let {
                it.renameVehicleGroupApi(renameVehicleGroup)
                assertEquals(
                    response, it.renameVehicleGroupApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test rename vehicle group api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(vehicleGroupMngmtResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.renameVehicleGroupApiCall(renameVehicleGroup))
                .thenReturn(vehicleGroupMngmtResponse)
            vehicleGroupMgmtViewModel?.let {
                it.renameVehicleGroupApi(renameVehicleGroup)
                assertEquals(
                    null, it.renameVehicleGroupApiVal.value?.data
                )
                assertEquals(
                    message, it.renameVehicleGroupApiVal.value?.errorMsg
                )
            }
        }
    }

//    @Test
//    fun `test delete vehicle group api call for success`() {
//        runBlockingTest {
//            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(true)
//            Mockito.lenient().`when`(vehicleGroupMngmtResponse.code()).thenReturn(200)
//            val response = VehicleGroupMngmtResponse(false, "", "")
//            Mockito.lenient().`when`(vehicleGroupMngmtResponse.body()).thenReturn(response)
//            Mockito.`when`(repository.deleteVehicleGroupApiCall(addDeleteVehicleGroup))
//                .thenReturn(vehicleGroupMngmtResponse)
//            vehicleGroupMgmtViewModel?.let {
//                it.deleteVehicleGroupApi(addDeleteVehicleGroup)
//                assertEquals(
//                    response, it.deleteVehicleGroupApiVal.value?.data
//                )
//            }
//        }
//    }
//
//    @Test
//    fun `test delete vehicle group api call for unknown error`() {
//        runBlockingTest {
//            val status = 403
//            val message = "Unknown error"
//            Mockito.lenient().`when`(vehicleGroupMngmtResponse.isSuccessful).thenReturn(false)
//            val testValidData = TestErrorResponseModel(
//                error = message,
//                exception = "exception",
//                message = message,
//                status = status,
//                errorCode = status,
//                timestamp = ""
//            )
//            val jsonString: String = Gson().toJson(testValidData)
//            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
//            Mockito.lenient().`when`(vehicleGroupMngmtResponse.errorBody()).thenReturn(responseBody)
//            Mockito.`when`(repository.deleteVehicleGroupApiCall(addDeleteVehicleGroup))
//                .thenReturn(vehicleGroupMngmtResponse)
//            vehicleGroupMgmtViewModel?.let {
//                it.deleteVehicleGroupApi(addDeleteVehicleGroup)
//                assertEquals(
//                    null, it.deleteVehicleGroupApiVal.value?.data
//                )
//                assertEquals(
//                    message, it.deleteVehicleGroupApiVal.value?.errorMsg
//                )
//            }
//        }
//    }

    @Test
    fun `test search vehicle of vehicle group api call for success`() {
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
            Mockito.`when`(repository.getSearchVehicleForGroupApiCall("", ""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getSearchVehiclesForGroup("", "")
                assertEquals(
                    vehicleList, it.searchVehicleVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test search vehicle of vehicle group api call for unknown error`() {
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
            Mockito.`when`(repository.getSearchVehicleForGroupApiCall("", ""))
                .thenReturn(vehicleListResponse)
            vehicleGroupMgmtViewModel?.let {
                it.getSearchVehiclesForGroup("", "")
                assertEquals(
                    null, it.searchVehicleVal.value?.data
                )
                assertEquals(
                    message, it.searchVehicleVal.value?.errorMsg
                )
            }
        }
    }

}