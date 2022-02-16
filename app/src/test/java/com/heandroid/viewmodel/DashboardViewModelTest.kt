package com.heandroid.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    private var dashboardViewModel: DashboardViewModel? = null
    private val authToken = "test token"
    private val agencyId = "test agency id"
    private val alertMessage = "test alert message"
    private val invalidTokenStatus = 401
    private val invalidTokenMessage = "Invalid token"
    private val unknownErrorStatus = 403
    private val unknownErrorMessage = "Unknown error"
    private val successStatus = 200

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var accountResponse: Response<AccountResponse>

    @Mock
    private lateinit var vehicleResponse: Response<List<VehicleResponse>>

    @Mock
    private lateinit var retrievePaymentListApiResponse: Response<RetrievePaymentListApiResponse>

    @Mock
    private lateinit var forgotUsernameApiResponse: Response<ForgotUsernameApiResponse>

    @Mock
    private lateinit var alertMessageApiResponse: Response<AlertMessageApiResponse>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        dashboardViewModel = DashboardViewModel(apiHelper)
    }

    @Test
    fun `test get account overview  api call for success`() {
        runBlockingTest {
            val accountRes = Mockito.mock(AccountResponse::class.java)
            Mockito.lenient().`when`(accountResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(accountResponse.code()).thenReturn(successStatus)
            Mockito.lenient().`when`(accountResponse.body()).thenReturn(accountRes)
            Mockito.`when`(apiHelper.getAccountOverviewApiCall(authToken))
                .thenReturn(accountResponse)
            dashboardViewModel?.let {
                it.getAccountOverViewApi(authToken)
                assertEquals(
                    Resource.success(accountResponse), it.accountOverviewVal.value
                )
                assertEquals(
                    accountResponse, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    accountRes, it.accountOverviewVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for invalid token error`() {
        runBlockingTest {
            val accountRes = Mockito.mock(AccountResponse::class.java)
            Mockito.lenient().`when`(accountResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(accountResponse.code()).thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(accountResponse.body()).thenReturn(accountRes)
            Mockito.`when`(apiHelper.getAccountOverviewApiCall(authToken))
                .thenReturn(accountResponse)
            dashboardViewModel?.let {
                it.getAccountOverViewApi(authToken)
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.accountOverviewVal.value
                )
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.accountOverviewVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown error`() {
        runBlockingTest {
            val accountRes = Mockito.mock(AccountResponse::class.java)
            Mockito.lenient().`when`(accountResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(accountResponse.code()).thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(accountResponse.body()).thenReturn(accountRes)
            Mockito.`when`(apiHelper.getAccountOverviewApiCall(authToken))
                .thenReturn(accountResponse)
            dashboardViewModel?.let {
                it.getAccountOverViewApi(authToken)
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.accountOverviewVal.value
                )
                assertEquals(
                    null, it.accountOverviewVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.accountOverviewVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get vehicle information api call for success`() {
        runBlockingTest {
            val vehicleRes = Mockito.mock(VehicleResponse::class.java)
            Mockito.lenient().`when`(vehicleResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(vehicleResponse.code()).thenReturn(successStatus)
            Mockito.lenient().`when`(vehicleResponse.body()).thenReturn(listOf(vehicleRes))
            Mockito.`when`(apiHelper.getVehicleListApiCall())
                .thenReturn(vehicleResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    Resource.success(vehicleResponse), it.vehicleListVal.value
                )
                assertEquals(
                    vehicleResponse, it.vehicleListVal.value?.data
                )
                assertEquals(
                    listOf(vehicleRes), it.vehicleListVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test get vehicle information api call for invalid token error`() {
        runBlockingTest {
            val vehicleRes = Mockito.mock(VehicleResponse::class.java)
            Mockito.lenient().`when`(vehicleResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(vehicleResponse.code()).thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(vehicleResponse.body()).thenReturn(listOf(vehicleRes))
            Mockito.`when`(apiHelper.getVehicleListApiCall())
                .thenReturn(vehicleResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.vehicleListVal.value
                )
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.vehicleListVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get vehicle information api call for unknown error`() {
        runBlockingTest {
            val vehicleRes = Mockito.mock(VehicleResponse::class.java)
            Mockito.lenient().`when`(vehicleResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(vehicleResponse.code()).thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(vehicleResponse.body()).thenReturn(listOf(vehicleRes))
            Mockito.`when`(apiHelper.getVehicleListApiCall())
                .thenReturn(vehicleResponse)
            dashboardViewModel?.let {
                it.getVehicleInformationApi()
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.vehicleListVal.value
                )
                assertEquals(
                    null, it.vehicleListVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.vehicleListVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test retrieve payment list api call for success`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(successStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.retrievePaymentList(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.retrievePaymentListApi(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.success(retrievePaymentListApiResponse), it.paymentListVal.value
                )
                assertEquals(
                    retrievePaymentListApiResponse, it.paymentListVal.value?.data
                )
                assertEquals(
                    retrievePaymentListRes, it.paymentListVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test  retrieve payment list api call for invalid token error`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.retrievePaymentList(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.retrievePaymentListApi(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.paymentListVal.value
                )
                assertEquals(
                    null, it.paymentListVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.paymentListVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test retrieve payment list api call for unknown error`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.retrievePaymentList(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.retrievePaymentListApi(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.paymentListVal.value
                )
                assertEquals(
                    null, it.paymentListVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.paymentListVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get monthly usage api call for success`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(successStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.getMonthlyUsageApiCall(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.getMonthlyUsage(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.success(retrievePaymentListApiResponse), it.monthlyUsageVal.value
                )
                assertEquals(
                    retrievePaymentListApiResponse, it.monthlyUsageVal.value?.data
                )
                assertEquals(
                    retrievePaymentListRes, it.monthlyUsageVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test get monthly usage api call for invalid token error`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.getMonthlyUsageApiCall(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.getMonthlyUsage(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.monthlyUsageVal.value
                )
                assertEquals(
                    null, it.monthlyUsageVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.monthlyUsageVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get monthly usage api call for unknown error`() {
        runBlockingTest {
            val retrievePaymentListRes = Mockito.mock(RetrievePaymentListApiResponse::class.java)
            val retrievePaymentListRequest = Mockito.mock(RetrievePaymentListRequest::class.java)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.code())
                .thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(retrievePaymentListApiResponse.body())
                .thenReturn(retrievePaymentListRes)
            Mockito.`when`(apiHelper.getMonthlyUsageApiCall(authToken, retrievePaymentListRequest))
                .thenReturn(retrievePaymentListApiResponse)
            dashboardViewModel?.let {
                it.getMonthlyUsage(authToken, retrievePaymentListRequest)
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.monthlyUsageVal.value
                )
                assertEquals(
                    null, it.monthlyUsageVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.monthlyUsageVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test recover username api call for success`() {
        runBlockingTest {
            val forgotUsernameApiRes = Mockito.mock(ForgotUsernameApiResponse::class.java)
            val forgotUsernameRequest = Mockito.mock(ForgotUsernameRequest::class.java)
            Mockito.lenient().`when`(forgotUsernameApiResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(forgotUsernameApiResponse.code())
                .thenReturn(successStatus)
            Mockito.lenient().`when`(forgotUsernameApiResponse.body())
                .thenReturn(forgotUsernameApiRes)
            Mockito.`when`(apiHelper.getForgotUserNameApiCall(agencyId, forgotUsernameRequest))
                .thenReturn(forgotUsernameApiResponse)
            dashboardViewModel?.let {
                it.recoverUsernameApi(agencyId, forgotUsernameRequest)
                assertEquals(
                    Resource.success(forgotUsernameApiResponse), it.forgotUsernameVal.value
                )
                assertEquals(
                    forgotUsernameApiResponse, it.forgotUsernameVal.value?.data
                )
                assertEquals(
                    forgotUsernameApiRes, it.forgotUsernameVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test recover username api call for invalid token error`() {
        runBlockingTest {
            val forgotUsernameApiRes = Mockito.mock(ForgotUsernameApiResponse::class.java)
            val forgotUsernameRequest = Mockito.mock(ForgotUsernameRequest::class.java)
            Mockito.lenient().`when`(forgotUsernameApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(forgotUsernameApiResponse.code())
                .thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(forgotUsernameApiResponse.body())
                .thenReturn(forgotUsernameApiRes)
            Mockito.`when`(apiHelper.getForgotUserNameApiCall(agencyId, forgotUsernameRequest))
                .thenReturn(forgotUsernameApiResponse)
            dashboardViewModel?.let {
                it.recoverUsernameApi(agencyId, forgotUsernameRequest)
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.forgotUsernameVal.value
                )
                assertEquals(
                    null, it.forgotUsernameVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.forgotUsernameVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test recover username api call for unknown error`() {
        runBlockingTest {
            val forgotUsernameApiRes = Mockito.mock(ForgotUsernameApiResponse::class.java)
            val forgotUsernameRequest = Mockito.mock(ForgotUsernameRequest::class.java)
            Mockito.lenient().`when`(forgotUsernameApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(forgotUsernameApiResponse.code())
                .thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(forgotUsernameApiResponse.body())
                .thenReturn(forgotUsernameApiRes)
            Mockito.`when`(apiHelper.getForgotUserNameApiCall(agencyId, forgotUsernameRequest))
                .thenReturn(forgotUsernameApiResponse)
            dashboardViewModel?.let {
                it.recoverUsernameApi(agencyId, forgotUsernameRequest)
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.forgotUsernameVal.value
                )
                assertEquals(
                    null, it.forgotUsernameVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.forgotUsernameVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get alert api call for success`() {
        runBlockingTest {
            val alertMessageApiRes = Mockito.mock(AlertMessageApiResponse::class.java)
            Mockito.lenient().`when`(alertMessageApiResponse.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(alertMessageApiResponse.code())
                .thenReturn(successStatus)
            Mockito.lenient().`when`(alertMessageApiResponse.body())
                .thenReturn(alertMessageApiRes)
            Mockito.`when`(apiHelper.getAlertMessageApiCAll(alertMessage))
                .thenReturn(alertMessageApiResponse)
            dashboardViewModel?.let {
                it.getAlertsApi(alertMessage)
                assertEquals(
                    Resource.success(alertMessageApiResponse), it.getAlertsVal.value
                )
                assertEquals(
                    alertMessageApiResponse, it.getAlertsVal.value?.data
                )
                assertEquals(
                    alertMessageApiRes, it.getAlertsVal.value?.data?.body()
                )
            }
        }
    }

    @Test
    fun `test get alert api call for invalid token error`() {
        runBlockingTest {
            val alertMessageApiRes = Mockito.mock(AlertMessageApiResponse::class.java)
            Mockito.lenient().`when`(alertMessageApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(alertMessageApiResponse.code())
                .thenReturn(invalidTokenStatus)
            Mockito.lenient().`when`(alertMessageApiResponse.body())
                .thenReturn(alertMessageApiRes)
            Mockito.`when`(apiHelper.getAlertMessageApiCAll(alertMessage))
                .thenReturn(alertMessageApiResponse)
            dashboardViewModel?.let {
                it.getAlertsApi(alertMessage)
                assertEquals(
                    Resource.error(null, invalidTokenMessage), it.getAlertsVal.value
                )
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    invalidTokenMessage, it.getAlertsVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test get alert api call for unknown error`() {
        runBlockingTest {
            val alertMessageApiRes = Mockito.mock(AlertMessageApiResponse::class.java)
            Mockito.lenient().`when`(alertMessageApiResponse.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(alertMessageApiResponse.code())
                .thenReturn(unknownErrorStatus)
            Mockito.lenient().`when`(alertMessageApiResponse.body())
                .thenReturn(alertMessageApiRes)
            Mockito.`when`(apiHelper.getAlertMessageApiCAll(alertMessage))
                .thenReturn(alertMessageApiResponse)
            dashboardViewModel?.let {
                it.getAlertsApi(alertMessage)
                assertEquals(
                    Resource.error(null, unknownErrorMessage), it.getAlertsVal.value
                )
                assertEquals(
                    null, it.getAlertsVal.value?.data
                )
                assertEquals(
                    unknownErrorMessage, it.getAlertsVal.value?.message
                )
            }
        }
    }
}