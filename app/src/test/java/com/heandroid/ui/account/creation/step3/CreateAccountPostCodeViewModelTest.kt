package com.heandroid.ui.account.creation.step3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.model.createaccount.EmailVerificationRequest
import com.heandroid.data.repository.account.AccountCreationRepository
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
class CreateAccountPostCodeViewModelTest {

    private var createAccountPostCodeViewModel: CreateAccountPostCodeViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: AccountCreationRepository

    @Mock
    private lateinit var addressesResponse: Response<List<DataAddress?>?>

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
        createAccountPostCodeViewModel = CreateAccountPostCodeViewModel(repository, errorManager)
    }

    @Test
    fun `test email verification api call for success`() {
        runTest {
            Mockito.lenient().`when`(addressesResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(addressesResponse.code()).thenReturn(200)
            val resp = listOf(DataAddress("", "", "", ""))
            Mockito.lenient().`when`(addressesResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getAddressListForPostalCode(""))
                .thenReturn(addressesResponse)
            createAccountPostCodeViewModel?.let {
                it.fetchAddress("")
                assertEquals(
                    resp, it.addresses.value?.data
                )
            }
        }
    }

    @Test
    fun `test update communication prefs api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(addressesResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(addressesResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getAddressListForPostalCode(""))
                .thenReturn(addressesResponse)
            createAccountPostCodeViewModel?.let {
                it.fetchAddress("")
                assertEquals(
                    null, it.addresses.value?.data
                )
                assertEquals(
                    message, it.addresses.value?.errorMsg
                )
            }
        }
    }
}