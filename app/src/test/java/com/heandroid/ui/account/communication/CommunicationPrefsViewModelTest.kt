package com.heandroid.ui.account.communication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.communicationspref.CommunicationPrefsResp
import com.heandroid.data.repository.communicationprefs.CommunicationPrefsRepo
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.data.DataFile
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
class CommunicationPrefsViewModelTest {

    private var communicationPrefsViewModel: CommunicationPrefsViewModel? = null

    private val communicationPrefsRequest = CommunicationPrefsRequestModel(arrayListOf())

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: CommunicationPrefsRepo

    @Mock
    private lateinit var communicationPrefsResponse: Response<CommunicationPrefsResp?>

    @Mock
    private lateinit var getAccountSettingsPrefsResponse: Response<AccountResponse?>

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
        communicationPrefsViewModel = CommunicationPrefsViewModel(repository, errorManager)
    }

    @Test
    fun `test update communication prefs api call for success`() {
        runTest {
            Mockito.lenient().`when`(communicationPrefsResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(communicationPrefsResponse.code()).thenReturn(200)
            val resp = CommunicationPrefsResp("", "")
            Mockito.lenient().`when`(communicationPrefsResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.updateCommunicationSettingsPrefs(communicationPrefsRequest))
                .thenReturn(communicationPrefsResponse)
            communicationPrefsViewModel?.let {
                it.updateCommunicationPrefs(communicationPrefsRequest)
                assertEquals(
                    resp, it.updateCommunicationPrefs.value?.data
                )
            }
        }
    }

    @Test
    fun `test update communication prefs api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(communicationPrefsResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(communicationPrefsResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.updateCommunicationSettingsPrefs(communicationPrefsRequest))
                .thenReturn(communicationPrefsResponse)
            communicationPrefsViewModel?.let {
                it.updateCommunicationPrefs(communicationPrefsRequest)
                assertEquals(
                    null, it.updateCommunicationPrefs.value?.data
                )
                assertEquals(
                    message, it.updateCommunicationPrefs.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get account prefs api call for success`() {
        runTest {
            Mockito.lenient().`when`(getAccountSettingsPrefsResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(getAccountSettingsPrefsResponse.code()).thenReturn(200)
            val resp = DataFile.getAccountResponse()
            Mockito.lenient().`when`(getAccountSettingsPrefsResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getAccountSettingsPrefs())
                .thenReturn(getAccountSettingsPrefsResponse)
            communicationPrefsViewModel?.let {
                it.getAccountSettingsPrefs()
                assertEquals(
                    resp, it.getAccountSettingsPrefs.value?.data
                )
            }
        }
    }

    @Test
    fun `test get account prefs api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(getAccountSettingsPrefsResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(getAccountSettingsPrefsResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.getAccountSettingsPrefs())
                .thenReturn(getAccountSettingsPrefsResponse)
            communicationPrefsViewModel?.let {
                it.getAccountSettingsPrefs()
                assertEquals(
                    null, it.getAccountSettingsPrefs.value?.data
                )
                assertEquals(
                    message, it.getAccountSettingsPrefs.value?.errorMsg
                )
            }
        }
    }
}