package com.conduent.nationalhighways.ui.bottomnav.account.payments.method

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.data.repository.payment.PaymentMethodRepository
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
class PaymentMethodViewModelTest {

    private var paymentMethodViewModel: PaymentMethodViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: PaymentMethodRepository

    @Mock
    private lateinit var paymentMethodResponse: Response<PaymentMethodResponseModel?>

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
        paymentMethodViewModel = PaymentMethodViewModel(repository, errorManager)
    }

    @Test
    fun `test save card list api call for success`() {
        runTest {
            Mockito.lenient().`when`(paymentMethodResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(paymentMethodResponse.code()).thenReturn(200)
            val resp = PaymentMethodResponseModel(null, "", "")
            Mockito.lenient().`when`(paymentMethodResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.savedCard())
                .thenReturn(paymentMethodResponse)
            paymentMethodViewModel?.let {
                it.saveCardList()
                assertEquals(
                    resp, it.savedCardList.value?.data
                )
            }
        }
    }

    @Test
    fun `test save card list api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(paymentMethodResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(paymentMethodResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.savedCard())
                .thenReturn(paymentMethodResponse)
            paymentMethodViewModel?.let {
                it.saveCardList()
                assertEquals(
                    null, it.savedCardList.value?.data
                )
                assertEquals(
                    message, it.savedCardList.value?.errorMsg
                )
            }
        }
    }
}