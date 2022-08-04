package com.heandroid.ui.bottomnav.account.payments.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.accountpayment.TransactionList
import com.heandroid.data.repository.paymenthistory.AccountPaymentHistoryRepo
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
class AccountPaymentHistoryViewModelTest {

    private var accountPaymentHistoryViewModel: AccountPaymentHistoryViewModel? = null

    private val accountPaymentHistoryRequest = AccountPaymentHistoryRequest()

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: AccountPaymentHistoryRepo

    @Mock
    private lateinit var accountPaymentHistoryResponse: Response<AccountPaymentHistoryResponse?>

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
        accountPaymentHistoryViewModel = AccountPaymentHistoryViewModel(repository, errorManager)
    }

    @Test
    fun `test get payment history details api call for success for no history`() {
        runTest {
            Mockito.lenient().`when`(accountPaymentHistoryResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(accountPaymentHistoryResponse.code()).thenReturn(200)
            val resp = AccountPaymentHistoryResponse(null, "", "")
            Mockito.lenient().`when`(accountPaymentHistoryResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getAccountPayment(accountPaymentHistoryRequest))
                .thenReturn(accountPaymentHistoryResponse)
            accountPaymentHistoryViewModel?.let {
                it.paymentHistoryDetails(accountPaymentHistoryRequest)
                assertEquals(
                    resp, it.paymentHistoryLiveData.value?.data
                )
            }
        }
    }

    @Test
    fun `test get payment history details api call for success`() {
        runTest {
            Mockito.lenient().`when`(accountPaymentHistoryResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(accountPaymentHistoryResponse.code()).thenReturn(200)
            val t1 = DataFile.getPaymentHistoryTransactionData()
            val t2 = DataFile.getPaymentHistoryTransactionData()
            val list = TransactionList(mutableListOf(t1, t2), "")
            val resp = AccountPaymentHistoryResponse(list, "", "")
            Mockito.lenient().`when`(accountPaymentHistoryResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getAccountPayment(accountPaymentHistoryRequest))
                .thenReturn(accountPaymentHistoryResponse)
            accountPaymentHistoryViewModel?.let {
                it.paymentHistoryDetails(accountPaymentHistoryRequest)
                assertEquals(
                    resp, it.paymentHistoryLiveData.value?.data
                )
                assertEquals(
                    list.transaction?.size, it.paymentHistoryLiveData.value?.data?.transactionList?.transaction?.size
                )
                assertEquals(
                    list.transaction, it.paymentHistoryLiveData.value?.data?.transactionList?.transaction
                )
            }
        }
    }

    @Test
    fun `test get payment history details api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(accountPaymentHistoryResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(accountPaymentHistoryResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getAccountPayment(accountPaymentHistoryRequest))
                .thenReturn(accountPaymentHistoryResponse)
            accountPaymentHistoryViewModel?.let {
                it.paymentHistoryDetails(accountPaymentHistoryRequest)
                assertEquals(
                    null, it.paymentHistoryLiveData.value?.data
                )
                assertEquals(
                    message, it.paymentHistoryLiveData.value?.errorMsg
                )
            }
        }
    }
}