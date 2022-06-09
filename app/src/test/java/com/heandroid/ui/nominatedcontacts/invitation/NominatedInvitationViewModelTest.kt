package com.heandroid.ui.nominatedcontacts.invitation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.nominatedcontacts.CreateAccountResponseModel
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.ui.vehicle.TestErrorResponseModel
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

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@MediumTest
class NominatedInvitationViewModelTest {

    private val createAccountRequestModel =
        com.heandroid.data.model.nominatedcontacts.CreateAccountRequestModel(
            "", "", "", "",
            "", "", ""
        )

    private var nominatedInvitationViewModel: NominatedInvitationViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: NominatedContactsRepo

    @Mock
    private lateinit var createAccountResponseModel: Response<CreateAccountResponseModel?>

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
        nominatedInvitationViewModel = NominatedInvitationViewModel(repository, errorManager)
    }

    @Test
    fun `test create account api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(createAccountResponseModel.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(createAccountResponseModel.code()).thenReturn(200)
            val resp = CreateAccountResponseModel(false, "", "", "", "", "")
            Mockito.lenient().`when`(createAccountResponseModel.body()).thenReturn(resp)
            Mockito.`when`(repository.createSecondaryAccount(createAccountRequestModel))
                .thenReturn(createAccountResponseModel)
            nominatedInvitationViewModel?.let {
                it.createAccount(createAccountRequestModel)
                assertEquals(
                    resp, it.createAccount.value?.data
                )
            }
        }
    }

    @Test
    fun `test create account api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(createAccountResponseModel.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(createAccountResponseModel.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.createSecondaryAccount(createAccountRequestModel))
                .thenReturn(createAccountResponseModel)
            nominatedInvitationViewModel?.let {
                it.createAccount(createAccountRequestModel)
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