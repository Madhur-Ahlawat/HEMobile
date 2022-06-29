package com.heandroid.ui.account.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.data.repository.profile.ProfileRepository
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
class ProfileViewModelTest {

    private var profileViewModel: ProfileViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ProfileRepository

    @Mock
    private lateinit var profileDetailResponse: Response<ProfileDetailModel?>

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
        profileViewModel = ProfileViewModel(repository, errorManager)
    }


    @Test
    fun `test reset password api call for success`() {
        runTest {
            Mockito.lenient().`when`(profileDetailResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(profileDetailResponse.code()).thenReturn(200)
            val resp = ProfileDetailModel(
                null, null, null,
                null, null, "", ""
            )
            Mockito.lenient().`when`(profileDetailResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.accountDetail())
                .thenReturn(profileDetailResponse)
            profileViewModel?.let {
                it.accountDetail()
                assertEquals(
                    resp, it.accountDetail.value?.data
                )
            }
        }
    }

    @Test
    fun `test reset password api call for unknown error`() {
        runTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(profileDetailResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(profileDetailResponse.errorBody())
                .thenReturn(responseBody)
            Mockito.`when`(repository.accountDetail())
                .thenReturn(profileDetailResponse)
            profileViewModel?.let {
                it.accountDetail()
                assertEquals(
                    null, it.accountDetail.value?.data
                )
                assertEquals(
                    message, it.accountDetail.value?.errorMsg
                )
            }
        }
    }
}