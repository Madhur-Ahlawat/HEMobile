package com.heandroid.ui.nominatedcontacts.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
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
class NominatedContactListViewModelTest {

    private var nominatedContactListViewModel: NominatedContactListViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: NominatedContactsRepo

    @Mock
    private lateinit var nominatedContactResponse: Response<NominatedContactRes?>

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
        nominatedContactListViewModel = NominatedContactListViewModel(repository, errorManager)
    }

    @Test
    fun `test get list of nominated contacts api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(nominatedContactResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(nominatedContactResponse.code()).thenReturn(200)
            val resp = NominatedContactRes(null,"", "")
            Mockito.lenient().`when`(nominatedContactResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getSecondaryAccount())
                .thenReturn(nominatedContactResponse)
            nominatedContactListViewModel?.let {
                it.nominatedContactList()
                assertEquals(
                    resp, it.contactList.value?.data
                )
            }
        }
    }

    @Test
    fun `test get list of nominated contacts api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(nominatedContactResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(nominatedContactResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getSecondaryAccount())
                .thenReturn(nominatedContactResponse)
            nominatedContactListViewModel?.let {
                it.nominatedContactList()
                assertEquals(
                    null, it.contactList.value?.data
                )
                assertEquals(
                    message, it.contactList.value?.errorMsg
                )
            }
        }
    }






}