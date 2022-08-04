package com.heandroid.ui.bottomnav.account.payments.topup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.accountpayment.AccountGetThresholdResponse
import com.heandroid.data.model.accountpayment.ThresholdAmountValue
import com.heandroid.data.repository.paymenthistory.AccountPaymentHistoryRepo
import com.heandroid.ui.vehicle.TestErrorResponseModel
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
class AccountTopUpPaymentViewModelTest {

    private var accountTopUpPaymentViewModel: AccountTopUpPaymentViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: AccountPaymentHistoryRepo

    @Mock
    private lateinit var accountGetThresholdResponse: Response<AccountGetThresholdResponse?>

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
        accountTopUpPaymentViewModel = AccountTopUpPaymentViewModel(repository, errorManager)
    }

    @Test
    fun `test payment with new card api call for success`() {
        runTest {
            Mockito.lenient().`when`(accountGetThresholdResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(accountGetThresholdResponse.code()).thenReturn(200)
            val resp = AccountGetThresholdResponse(ThresholdAmountValue("", "", "", ""), "", "")
            Mockito.lenient().`when`(accountGetThresholdResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getThresholdAmount())
                .thenReturn(accountGetThresholdResponse)
            accountTopUpPaymentViewModel?.let {
                it.getThresholdAmount()
                assertEquals(
                    resp, it.thresholdLiveData.value?.data
                )
            }
        }
    }

    @Test
    fun `test payment with new card api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(accountGetThresholdResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(accountGetThresholdResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getThresholdAmount())
                .thenReturn(accountGetThresholdResponse)
            accountTopUpPaymentViewModel?.let {
                it.getThresholdAmount()
                assertEquals(
                    null, it.thresholdLiveData.value?.data
                )
                assertEquals(
                    message, it.thresholdLiveData.value?.errorMsg
                )
            }
        }
    }
}