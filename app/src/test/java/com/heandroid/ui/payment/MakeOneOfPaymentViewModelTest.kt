package com.heandroid.ui.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelRequest
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.heandroid.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.data.repository.makeoneofpayments.MakeOneOfPaymentRepo
import com.heandroid.data.repository.profile.ProfileRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.data.DataFile
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
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@LargeTest
class MakeOneOfPaymentViewModelTest {

    private var makeOneOfPaymentViewModel: MakeOneOfPaymentViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: MakeOneOfPaymentRepo

    @Mock
    private lateinit var crossingDetailsModelResponse: Response<CrossingDetailsModelsResponse?>

    @Mock
    private lateinit var whereToReceivePaymentResponse: Response<ResponseBody?>

    @Mock
    private lateinit var oneOfPaymentModelResponse: Response<OneOfPaymentModelResponse?>

    @Inject
    lateinit var errorManager: ErrorManager

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val crossingDetailsRequest = CrossingDetailsModelsRequest(
        "", "", "", "", ""
    )
    private val PaymentReceiptRequest = PaymentReceiptDeliveryTypeSelectionRequest(
        "", ""
    )

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        makeOneOfPaymentViewModel = MakeOneOfPaymentViewModel(repository, errorManager)
    }

    @Test
    fun `test crossing details api call for success`() {
        runTest {
            Mockito.lenient().`when`(crossingDetailsModelResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(crossingDetailsModelResponse.code()).thenReturn(200)
            val resp = DataFile.getCrossingDetailsResponse()
            Mockito.lenient().`when`(crossingDetailsModelResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getCrossingDetails(crossingDetailsRequest))
                .thenReturn(crossingDetailsModelResponse)
            makeOneOfPaymentViewModel?.let {
                it.getCrossingDetails(crossingDetailsRequest)
                assertEquals(
                    resp, it.getCrossingDetails.value?.data
                )
            }
        }
    }

    @Test
    fun `test crossing details api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(crossingDetailsModelResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(crossingDetailsModelResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getCrossingDetails(crossingDetailsRequest))
                .thenReturn(crossingDetailsModelResponse)
            makeOneOfPaymentViewModel?.let {
                it.getCrossingDetails(crossingDetailsRequest)
                assertEquals(
                    null, it.getCrossingDetails.value?.data
                )
                assertEquals(
                    message, it.getCrossingDetails.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test where to receive payment api call for success`() {
        runTest {
            Mockito.lenient().`when`(oneOfPaymentModelResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(oneOfPaymentModelResponse.code()).thenReturn(200)
            val resp = OneOfPaymentModelResponse(0, "")
            Mockito.lenient().`when`(oneOfPaymentModelResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.oneOfPaymentsPay(DataFile.getOneOfPaymentModelRequest()))
                .thenReturn(oneOfPaymentModelResponse)
            makeOneOfPaymentViewModel?.let {
                it.oneOfPaymentsPay(DataFile.getOneOfPaymentModelRequest())
                assertEquals(
                    resp, it.oneOfPaymentsPay.value?.data
                )
            }
        }
    }

    @Test
    fun `test where to receive payment api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(oneOfPaymentModelResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(oneOfPaymentModelResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.oneOfPaymentsPay(DataFile.getOneOfPaymentModelRequest()))
                .thenReturn(oneOfPaymentModelResponse)
            makeOneOfPaymentViewModel?.let {
                it.oneOfPaymentsPay(DataFile.getOneOfPaymentModelRequest())
                assertEquals(
                    null, it.oneOfPaymentsPay.value?.data
                )
                assertEquals(
                    message, it.oneOfPaymentsPay.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test one off payment api call for success`() {
        runTest {
            Mockito.lenient().`when`(whereToReceivePaymentResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(whereToReceivePaymentResponse.code()).thenReturn(200)
            val resp = Mockito.mock(ResponseBody::class.java)
            Mockito.lenient().`when`(whereToReceivePaymentResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.whereToReceivePaymentReceipt(PaymentReceiptRequest))
                .thenReturn(whereToReceivePaymentResponse)
            makeOneOfPaymentViewModel?.let {
                it.whereToReceivePaymentReceipt(PaymentReceiptRequest)
                assertEquals(
                    resp, it.whereToReceivePaymentReceipt.value?.data
                )
            }
        }
    }

    @Test
    fun `test one off payment api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(whereToReceivePaymentResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(whereToReceivePaymentResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.whereToReceivePaymentReceipt(PaymentReceiptRequest))
                .thenReturn(whereToReceivePaymentResponse)
            makeOneOfPaymentViewModel?.let {
                it.whereToReceivePaymentReceipt(PaymentReceiptRequest)
                assertEquals(
                    null, it.whereToReceivePaymentReceipt.value?.data
                )
                assertEquals(
                    message, it.whereToReceivePaymentReceipt.value?.errorMsg
                )
            }
        }
    }
}