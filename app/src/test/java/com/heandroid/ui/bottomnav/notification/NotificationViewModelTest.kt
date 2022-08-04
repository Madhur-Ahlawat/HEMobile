package com.heandroid.ui.bottomnav.notification

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.nominatedcontacts.CreateAccountResponseModel
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.data.repository.notification.NotificationRepo
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
class NotificationViewModelTest {

    private var notificationViewModel: NotificationViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: NotificationRepo

    @Mock
    private lateinit var alertMessageApiResponse: Response<AlertMessageApiResponse?>

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
        notificationViewModel = NotificationViewModel(repository, errorManager)
    }

    @Test
    fun `test get alert api call for success`() {
        runTest {
            Mockito.lenient().`when`(alertMessageApiResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(alertMessageApiResponse.code()).thenReturn(200)
            val resp = AlertMessageApiResponse(0, "", null)
            Mockito.lenient().`when`(alertMessageApiResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getAlertMessages())
                .thenReturn(alertMessageApiResponse)
            notificationViewModel?.let {
                it.getAlertsApi("")
                assertEquals(
                    resp, it.alertLivData.value?.data
                )
            }
        }
    }

    @Test
    fun `test get alert api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(alertMessageApiResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(alertMessageApiResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getAlertMessages())
                .thenReturn(alertMessageApiResponse)
            notificationViewModel?.let {
                it.getAlertsApi("")
                assertEquals(
                    null, it.alertLivData.value?.data
                )
                assertEquals(
                    message, it.alertLivData.value?.errorMsg
                )
            }
        }
    }
}