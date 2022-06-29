package com.heandroid.ui.account.creation.step4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.repository.auth.CreateAccountRespository
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
class CreateAccountVehicleViewModelTest {

    private var createAccountVehicleViewModel: CreateAccountVehicleViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: CreateAccountRespository

    @Mock
    private lateinit var findResponse: Response<VehicleInfoDetails?>

    @Mock
    private lateinit var validResponse: Response<String?>

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
        createAccountVehicleViewModel = CreateAccountVehicleViewModel(repository, errorManager)
    }

    @Test
    fun `test find vehicle api call for success`() {
        runTest {
            Mockito.lenient().`when`(findResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(findResponse.code()).thenReturn(200)
            val resp = VehicleInfoDetails(
                RetrievePlateInfoDetails(
                    "", "",
                    "", "", ""
                )
            )
            Mockito.lenient().`when`(findResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getVehicleDetail("", 0))
                .thenReturn(findResponse)
            createAccountVehicleViewModel?.let {
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
            Mockito.lenient().`when`(findResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(findResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getVehicleDetail("", 0))
                .thenReturn(findResponse)
            createAccountVehicleViewModel?.let {
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
    fun `test validate vehicle api call for success`() {
        runTest {
            Mockito.lenient().`when`(validResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(validResponse.code()).thenReturn(200)

            val request = ValidVehicleCheckRequest(
                "", "",
                "", "", "", "", ""
            )
            Mockito.lenient().`when`(validResponse.body()).thenReturn("")
            Mockito.`when`(repository.validVehicleCheck(request, 0))
                .thenReturn(validResponse)
            createAccountVehicleViewModel?.let {
                it.validVehicleCheck(request, 0)
                assertEquals(
                    "", it.validVehicleLiveData.value?.data
                )
            }
        }
    }

    @Test
    fun `test validate vehicle api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(validResponse.isSuccessful).thenReturn(false)
            val testValidData = TestErrorResponseModel(
                error = message,
                exception = "exception",
                message = message,
                status = status,
                errorCode = status,
                timestamp = ""
            )
            val request = ValidVehicleCheckRequest(
                "", "",
                "", "", "", "", ""
            )
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(validResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.validVehicleCheck(request, 0))
                .thenReturn(validResponse)
            createAccountVehicleViewModel?.let {
                it.validVehicleCheck(request, 0)
                assertEquals(
                    null, it.validVehicleLiveData.value?.data
                )
                assertEquals(
                    message, it.validVehicleLiveData.value?.errorMsg
                )
            }
        }
    }
}