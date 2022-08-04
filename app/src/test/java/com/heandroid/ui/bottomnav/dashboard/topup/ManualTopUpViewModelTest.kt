package com.heandroid.ui.bottomnav.dashboard.topup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.payment.PaymentMethodDeleteResponseModel
import com.heandroid.data.repository.manualtopup.ManualTopUpRepository
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
@MediumTest
class ManualTopUpViewModelTest {

    private var manualTopUpViewModel: ManualTopUpViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ManualTopUpRepository

    @Mock
    private lateinit var paymentMethodDeleteResponse: Response<PaymentMethodDeleteResponseModel?>

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
        manualTopUpViewModel = ManualTopUpViewModel(repository, errorManager)
    }

    @Test
    fun `test payment with new card api call for success`() {
        runTest {
            Mockito.lenient().`when`(paymentMethodDeleteResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(paymentMethodDeleteResponse.code()).thenReturn(200)
            val resp = PaymentMethodDeleteResponseModel("", "","", "", null)
            Mockito.lenient().`when`(paymentMethodDeleteResponse.body()).thenReturn(resp)
            val request = DataFile.getPaymentWithNewCardRequest()
            Mockito.`when`(repository.paymentWithNewCard(request))
                .thenReturn(paymentMethodDeleteResponse)
            manualTopUpViewModel?.let {
                it.paymentWithNewCard(request)
                assertEquals(
                    resp, it.paymentWithNewCard.value?.data
                )
            }
        }
    }

    @Test
    fun `test payment with new card api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(paymentMethodDeleteResponse.isSuccessful).thenReturn(false)
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
            val request = DataFile.getPaymentWithNewCardRequest()
            Mockito.lenient().`when`(paymentMethodDeleteResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.paymentWithNewCard(request))
                .thenReturn(paymentMethodDeleteResponse)
            manualTopUpViewModel?.let {
                it.paymentWithNewCard(request)
                assertEquals(
                    null, it.paymentWithNewCard.value?.data
                )
                assertEquals(
                    message, it.paymentWithNewCard.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test payment with existing card api call for success`() {
        runTest {
            Mockito.lenient().`when`(paymentMethodDeleteResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(paymentMethodDeleteResponse.code()).thenReturn(200)
            val resp = PaymentMethodDeleteResponseModel("", "","", "", null)
            Mockito.lenient().`when`(paymentMethodDeleteResponse.body()).thenReturn(resp)
            val request = DataFile.getPaymentWithExistingCardRequest()
            Mockito.`when`(repository.paymentWithExistingCard(request))
                .thenReturn(paymentMethodDeleteResponse)
            manualTopUpViewModel?.let {
                it.paymentWithExistingCard(request)
                assertEquals(
                    resp, it.paymentWithExistingCard.value?.data
                )
            }
        }
    }

    @Test
    fun `test payment with existing card api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(paymentMethodDeleteResponse.isSuccessful).thenReturn(false)
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
            val request = DataFile.getPaymentWithExistingCardRequest()
            Mockito.lenient().`when`(paymentMethodDeleteResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.paymentWithExistingCard(request))
                .thenReturn(paymentMethodDeleteResponse)
            manualTopUpViewModel?.let {
                it.paymentWithExistingCard(request)
                assertEquals(
                    null, it.paymentWithExistingCard.value?.data
                )
                assertEquals(
                    message, it.paymentWithExistingCard.value?.errorMsg
                )
            }
        }
    }
}