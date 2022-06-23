package com.heandroid.ui.account.communication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.communicationspref.CommunicationPrefsResp
import com.heandroid.data.repository.communicationprefs.CommunicationPrefsRepo
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.DateUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
    lateinit var errorManager: ErrorManager

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dateUtils: DateUtils


    fun `test date range`() {
        dateUtils.calculateDays("01/30/2022", "01/20/2020")
        assertEquals("","")
    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        communicationPrefsViewModel = CommunicationPrefsViewModel(repository, errorManager)
        dateUtils = DateUtils
    }


    //@Test
    fun `test reset password api call for success`() {
        runBlockingTest {
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

    //@Test
    fun `test reset password api call for unknown error`() {
        runBlockingTest {
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
}