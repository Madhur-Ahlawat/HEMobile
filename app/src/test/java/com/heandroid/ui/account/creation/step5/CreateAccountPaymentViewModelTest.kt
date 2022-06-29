package com.heandroid.ui.account.creation.step5

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.*
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.data.DataFile
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
class CreateAccountPaymentViewModelTest {

    private var createAccountPaymentViewModel: CreateAccountPaymentViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: CreateAccountRespository

    @Mock
    private lateinit var createAccountResponse: Response<CreateAccountResponseModel?>

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
        createAccountPaymentViewModel = CreateAccountPaymentViewModel(repository, errorManager)
    }

    @Test
    fun `test find vehicle api call for success`() {
        runTest {
            Mockito.lenient().`when`(createAccountResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(createAccountResponse.code()).thenReturn(200)
            val resp = CreateAccountResponseModel(
                "", "",
                "", "", ""
            )
            val request = DataFile.getCreateAccountRequestModel()
            Mockito.lenient().`when`(createAccountResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.createAccount(request))
                .thenReturn(createAccountResponse)
            createAccountPaymentViewModel?.let {
                it.createAccount(request)
                assertEquals(
                    resp, it.createAccount.value?.data
                )
            }
        }
    }

    @Test
    fun `test find vehicle api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(createAccountResponse.isSuccessful).thenReturn(false)
            val testValidData = TestErrorResponseModel(
                error = message,
                exception = "exception",
                message = message,
                status = status,
                errorCode = status,
                timestamp = ""
            )
            val request = DataFile.getCreateAccountRequestModel()
            val jsonString: String = Gson().toJson(testValidData)
            Mockito.lenient().`when`(responseBody.string()).thenReturn(jsonString)
            Mockito.lenient().`when`(createAccountResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.createAccount(request))
                .thenReturn(createAccountResponse)
            createAccountPaymentViewModel?.let {
                it.createAccount(request)
                assertEquals(
                    null, it.createAccount.value?.data
                )
                assertEquals(
                    message, it.createAccount.value?.errorMsg
                )
            }
        }
    }
}