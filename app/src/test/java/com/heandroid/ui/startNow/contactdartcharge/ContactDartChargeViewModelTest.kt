package com.heandroid.ui.startNow.contactdartcharge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.contactdartcharge.*
import com.heandroid.data.repository.contactdartcharge.ContactDartChargeRepository
import com.heandroid.ui.vehicle.TestErrorResponseModel
import com.heandroid.utils.data.DataFile
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MultipartBody
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
class ContactDartChargeViewModelTest {

    private val caseEnquiryHistoryRequest: CaseEnquiryHistoryRequest =
        CaseEnquiryHistoryRequest("", "")

    private val createNewCaseReq: CreateNewCaseReq =
        CreateNewCaseReq("", "", "", "", "",
            "", "", "", null, "")

    private var contactDartChargeViewModel: ContactDartChargeViewModel? = null

    @Mock
    private lateinit var responseBody: ResponseBody

    @Mock
    private lateinit var repository: ContactDartChargeRepository

    @Mock
    private lateinit var caseHistoryListResponse: Response<CaseEnquiryHistoryResponse?>

    @Mock
    private lateinit var createNewCaseResponse: Response<CreateNewCaseResp?>

    @Mock
    private lateinit var uploadFileResponseModel: Response<UploadFileResponseModel?>

    @Mock
    private lateinit var uploadFileRequest: MultipartBody.Part

    @Mock
    private var caseCategoriesListResponse: Response<List<CaseCategoriesModel?>?>? = null

    @Mock
    private var caseSubCategoriesListResponse: Response<List<CaseCategoriesModel?>?>? = null

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
        contactDartChargeViewModel = ContactDartChargeViewModel(repository, errorManager)
    }

    @Test
    fun `test get list of case history api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseHistoryListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseHistoryListResponse.code()).thenReturn(200)
            val request1 = DataFile.getServiceRequest("123")
            val request2 = DataFile.getServiceRequest("456")
            val list = listOf(request1, request2)
            val res = ServiceRequestList(list, "")
            val resp = CaseEnquiryHistoryResponse(res, "", "")
            Mockito.lenient().`when`(caseHistoryListResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getCaseHistoryDataApiCall(caseEnquiryHistoryRequest))
                .thenReturn(caseHistoryListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseHistoryData(caseEnquiryHistoryRequest)
                assertEquals(
                    resp, it.caseHistoryApiVal.value?.data
                )
                assertEquals(
                    list, it.caseHistoryApiVal.value?.data?.serviceRequestList?.serviceRequest
                )
            }
        }
    }


    @Test
    fun `test get list of case history api call for success no history`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseHistoryListResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseHistoryListResponse.code()).thenReturn(200)
            val resp = CaseEnquiryHistoryResponse(null, "", "")
            Mockito.lenient().`when`(caseHistoryListResponse.body()).thenReturn(resp)
            Mockito.`when`(repository.getCaseHistoryDataApiCall(caseEnquiryHistoryRequest))
                .thenReturn(caseHistoryListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseHistoryData(caseEnquiryHistoryRequest)
                assertEquals(
                    resp, it.caseHistoryApiVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get list of case history api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(caseHistoryListResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(caseHistoryListResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getCaseHistoryDataApiCall(caseEnquiryHistoryRequest))
                .thenReturn(caseHistoryListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseHistoryData(caseEnquiryHistoryRequest)
                assertEquals(
                    null, it.caseHistoryApiVal.value?.data
                )
                assertEquals(
                    message, it.caseHistoryApiVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get list of case categories api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseCategoriesListResponse?.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseCategoriesListResponse?.code()).thenReturn(200)
            val request1 = CaseCategoriesModel("123", "")
            val request2 = CaseCategoriesModel("456", "")
            val list = listOf(request1, request2)
            Mockito.lenient().`when`(caseCategoriesListResponse?.body()).thenReturn(list)
            Mockito.`when`(repository.getCaseCategoriesList())
                .thenReturn(caseCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseCategoriesList()
                assertEquals(
                    list, it.getCaseCategoriesListVal.value?.data
                )
                assertEquals(
                    list.size, it.getCaseCategoriesListVal.value?.data?.size
                )
            }
        }
    }


    @Test
    fun `test get list of case categories api call for success no categories list`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseCategoriesListResponse?.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseCategoriesListResponse?.code()).thenReturn(200)
            val list = listOf<CaseCategoriesModel>( )
            Mockito.lenient().`when`(caseCategoriesListResponse?.body()).thenReturn(list)
            Mockito.`when`(repository.getCaseCategoriesList())
                .thenReturn(caseCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseCategoriesList()
                assertEquals(
                    list, it.getCaseCategoriesListVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get list of case categories api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(caseCategoriesListResponse?.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(caseCategoriesListResponse?.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getCaseCategoriesList())
                .thenReturn(caseCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseCategoriesList()
                assertEquals(
                    null, it.getCaseCategoriesListVal.value?.data
                )
                assertEquals(
                    message, it.getCaseCategoriesListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test get list of case sub categories api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.code()).thenReturn(200)
            val request1 = CaseCategoriesModel("123", "")
            val request2 = CaseCategoriesModel("456", "")
            val list = listOf(request1, request2)
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.body()).thenReturn(list)
            Mockito.`when`(repository.getCaseSubCategoriesList(""))
                .thenReturn(caseSubCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseSubCategoriesList("")
                assertEquals(
                    list, it.getCaseSubCategoriesListVal.value?.data
                )
                assertEquals(
                    list.size, it.getCaseSubCategoriesListVal.value?.data?.size
                )
            }
        }
    }


    @Test
    fun `test get list of case sub categories api call for success no categories list`() {
        runBlockingTest {
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.code()).thenReturn(200)
            val list = listOf<CaseCategoriesModel>( )
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.body()).thenReturn(list)
            Mockito.`when`(repository.getCaseSubCategoriesList(""))
                .thenReturn(caseSubCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseSubCategoriesList("")
                assertEquals(
                    list, it.getCaseSubCategoriesListVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test get list of case sub categories api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(caseSubCategoriesListResponse?.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.getCaseSubCategoriesList(""))
                .thenReturn(caseSubCategoriesListResponse)
            contactDartChargeViewModel?.let {
                it.getCaseSubCategoriesList("")
                assertEquals(
                    null, it.getCaseSubCategoriesListVal.value?.data
                )
                assertEquals(
                    message, it.getCaseSubCategoriesListVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test create new case api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(createNewCaseResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(createNewCaseResponse.code()).thenReturn(200)
            val res = CreateNewCaseResp("", "", "", "", "")
            Mockito.lenient().`when`(createNewCaseResponse.body()).thenReturn(res)
            Mockito.`when`(repository.createNewCase(createNewCaseReq))
                .thenReturn(createNewCaseResponse)
            contactDartChargeViewModel?.let {
                it.createNewCase(createNewCaseReq)
                assertEquals(
                    res, it.createNewCaseVal.value?.data
                )
            }
        }
    }

    @Test
    fun `test create new case api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(createNewCaseResponse.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(createNewCaseResponse.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.createNewCase(createNewCaseReq))
                .thenReturn(createNewCaseResponse)
            contactDartChargeViewModel?.let {
                it.createNewCase(createNewCaseReq)
                assertEquals(
                    null, it.createNewCaseVal.value?.data
                )
                assertEquals(
                    message, it.createNewCaseVal.value?.errorMsg
                )
            }
        }
    }

    @Test
    fun `test upload file api call for success`() {
        runBlockingTest {
            Mockito.lenient().`when`(uploadFileResponseModel.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(uploadFileResponseModel.code()).thenReturn(200)
            val res = UploadFileResponseModel(true, "", "", "")
            Mockito.lenient().`when`(uploadFileResponseModel.body()).thenReturn(res)
            Mockito.`when`(repository.uploadFile(uploadFileRequest))
                .thenReturn(uploadFileResponseModel)
            contactDartChargeViewModel?.let {
                it.uploadFileApi(uploadFileRequest)
                assertEquals(
                    res, it.uploadFileVal.value?.data
                )
                assertEquals(
                    true, it.uploadFileVal.value?.data?.uploaded
                )
            }
        }
    }

    @Test
    fun `test upload file api call for failure`() {
        runBlockingTest {
            Mockito.lenient().`when`(uploadFileResponseModel.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(uploadFileResponseModel.code()).thenReturn(200)
            val res = UploadFileResponseModel(false, "", "", "")
            Mockito.lenient().`when`(uploadFileResponseModel.body()).thenReturn(res)
            Mockito.`when`(repository.uploadFile(uploadFileRequest))
                .thenReturn(uploadFileResponseModel)
            contactDartChargeViewModel?.let {
                it.uploadFileApi(uploadFileRequest)
                assertEquals(
                    res, it.uploadFileVal.value?.data
                )
                assertEquals(
                    false, it.uploadFileVal.value?.data?.uploaded
                )
            }
        }
    }

    @Test
    fun `test upload file api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(uploadFileResponseModel.isSuccessful).thenReturn(false)
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
            Mockito.lenient().`when`(uploadFileResponseModel.errorBody()).thenReturn(responseBody)
            Mockito.`when`(repository.uploadFile(uploadFileRequest))
                .thenReturn(uploadFileResponseModel)
            contactDartChargeViewModel?.let {
                it.uploadFileApi(uploadFileRequest)
                assertEquals(
                    null, it.uploadFileVal.value?.data
                )
                assertEquals(
                    message, it.uploadFileVal.value?.errorMsg
                )
            }
        }
    }



}