package com.heandroid.ui.checkpaidcrossings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.data.repository.checkpaidcrossings.CheckPaidCrossingsRepo
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
class CheckPaidCrossingViewModelTest {

    private var checkPaidCrossingViewModel: CheckPaidCrossingViewModel? = null

    private val unknownException = "unknown exception"

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: CheckPaidCrossingsRepo

    @Mock
    private lateinit var finVehicleResponse: Response<VehicleInfoDetails?>

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
        checkPaidCrossingViewModel = CheckPaidCrossingViewModel(repository, errorManager)
    }


    @Test
    fun `test find vehicle api call for success`() {
        runTest {
            Mockito.lenient().`when`(finVehicleResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(finVehicleResponse.code()).thenReturn(200)
            val resp = VehicleInfoDetails(null)
            Mockito.lenient().`when`(finVehicleResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getVehicleDetail("", 0))
                .thenReturn(finVehicleResponse)
            checkPaidCrossingViewModel?.let {
                it.getVehicleData("", 0)
                assertEquals(
                    resp, it.findVehicleLiveData.value?.data
                )
            }
        }
    }

    @Test
    fun `test find vehicle api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(finVehicleResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(finVehicleResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleDetail("", 0))
                .thenReturn(finVehicleResponse)
            checkPaidCrossingViewModel?.let {
                it.getVehicleData("", 0)
                assertEquals(
                    null, it.findVehicleLiveData.value?.data
                )
                assertEquals(
                    message, it.findVehicleLiveData.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test find vehicle api call for unknown exception`() {
        runTest {
            Mockito.`when`(repository.getVehicleDetail("", 0))
                .thenAnswer {
                    throw Exception(unknownException)
                }
            checkPaidCrossingViewModel?.let {
                it.getVehicleData("", 0)
                assertEquals(
                    null, it.findVehicleLiveData.value?.data
                )
                assertEquals(
                    unknownException, it.findVehicleLiveData.value?.errorMsg
                )
            }
        }
    }


    @Test
    fun `test set paid crossing option`() {
        runTest {
            checkPaidCrossingViewModel?.let {
                it.setPaidCrossingOption(CheckPaidCrossingsOptionsModel("11", "22", false))
                assertEquals(
                    "11", it.paidCrossingOption.value?.ref
                )
                assertEquals(
                    "22", it.paidCrossingOption.value?.vrm
                )
                assertEquals(
                    false, it.paidCrossingOption.value?.enable
                )
            }
        }
    }


    @Test
    fun `test set paid crossing response`() {
        runTest {
            checkPaidCrossingViewModel?.let {
                it.setPaidCrossingResponse(CheckPaidCrossingsResponse("11223344", "account type", "type",
                    "active", "", "UK","11", "2022"))
                assertEquals(
                    "11223344", it.paidCrossingResponse.value?.accountNo
                )
                assertEquals(
                    "account type", it.paidCrossingResponse.value?.accountTypeCd
                )
                assertEquals(
                    "type", it.paidCrossingResponse.value?.accountType
                )
            }
        }
    }
}