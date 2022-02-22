package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountResponse
import com.heandroid.model.RetrievePaymentListApiResponse
import com.heandroid.model.RetrievePaymentListRequest
import com.heandroid.model.VehicleResponse
import com.heandroid.oldStructure.network.ApiHelper
import com.heandroid.utils.common.Resource
import com.heandroid.viewmodel.DashboardViewModel
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DashboardNetworkCallViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var apiUsersObserver: Observer<Resource<Response<AccountResponse>>>

    @Mock
    private lateinit var apiVehicleListObserver: Observer<Resource<Response<List<VehicleResponse>>>>

    @Mock
    private lateinit var apiRetrievePaymentObserver: Observer<Resource<Response<RetrievePaymentListApiResponse>>>

    @Mock
    private lateinit var apiMonthlyUsageObserver : Observer<Resource<Response<RetrievePaymentListApiResponse>>>

    @Mock
    private lateinit var apiloginresponse: Response<AccountResponse>

    @Mock
    private lateinit var vehicleApiResponse: List<VehicleResponse>


    @Mock
    private lateinit var monthlyUsageApiResponse : Response<RetrievePaymentListApiResponse>

    @Before
    fun setUp() {
        // do something if required
    }

    @Test
    fun givenServerResponse200_whenAccountOverViewFetch_shouldReturnSuccess() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val accountOverviewResp = Mockito.mock(AccountResponse::class.java)
        Mockito.`when`(resp.isSuccessful()).thenReturn(true)
        Mockito.`when`(resp.code()).thenReturn(200)
        Mockito.`when`(resp.body()).thenReturn(accountOverviewResp)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getAccountOverviewApiCall(accessToken)
            viewModel.getAccountOverViewApi(accessToken)
            viewModel.accountOverviewVal.observeForever(apiUsersObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.accountOverviewVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(200, viewModel.accountOverviewVal.value!!.data!!.code())
            TestCase.assertEquals(true, viewModel.accountOverviewVal.value!!.data!!.isSuccessful)
            TestCase.assertEquals(
                accountOverviewResp,
                viewModel.accountOverviewVal.value!!.data!!.body()
            )

//            Mockito.verify(apiHelper).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
//            viewModel.accountOverviewVal.removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenAccountOverViewFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val accountOverviewResp = Mockito.mock(AccountResponse::class.java)
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(401)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getAccountOverviewApiCall(accessToken)
            viewModel.getAccountOverViewApi(accessToken)
            viewModel.accountOverviewVal.observeForever(apiUsersObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.accountOverviewVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.accountOverviewVal.value!!.data)
            TestCase.assertEquals("Invalid token", viewModel.accountOverviewVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

    @Test
    fun givenServerResponseUnknownError_whenAccountOverViewFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val accountOverviewResp = Mockito.mock(AccountResponse::class.java)
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(1234)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getAccountOverviewApiCall(accessToken)
            viewModel.getAccountOverViewApi(accessToken)
            viewModel.accountOverviewVal.observeForever(apiUsersObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.accountOverviewVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.accountOverviewVal.value!!.data)
            TestCase.assertEquals("Unknown error", viewModel.accountOverviewVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

    @Test
    fun givenServerResponse200_whenVehicleListFetch_shouldReturnSuccess() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)

        Mockito.`when`(resp.isSuccessful()).thenReturn(true)
        Mockito.`when`(resp.code()).thenReturn(200)
        Mockito.`when`(resp.body()).thenReturn(vehicleApiResponse)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getVehicleListApiCall(accessToken)
            viewModel.getVehicleInformationApi(accessToken)
            viewModel.vehicleListVal.observeForever(apiVehicleListObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.vehicleListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(200, viewModel.vehicleListVal.value!!.data!!.code())
            TestCase.assertEquals(true, viewModel.vehicleListVal.value!!.data!!.isSuccessful)
            TestCase.assertEquals(
                vehicleApiResponse,
                viewModel.vehicleListVal.value!!.data!!.body()
            )

//            Mockito.verify(apiHelper).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
//            viewModel.accountOverviewVal.removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenVehicleListFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)

        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(401)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getVehicleListApiCall(accessToken)
            viewModel.getVehicleInformationApi(accessToken)
            viewModel.vehicleListVal.observeForever(apiVehicleListObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.vehicleListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.vehicleListVal.value!!.data)
            TestCase.assertEquals("Invalid token", viewModel.vehicleListVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

    @Test
    fun givenServerResponseUnknownError_whenVehicleListFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)

        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(5501)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getVehicleListApiCall(accessToken)
            viewModel.getVehicleInformationApi(accessToken)
            viewModel.vehicleListVal.observeForever(apiVehicleListObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.vehicleListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.vehicleListVal.value!!.data)
            TestCase.assertEquals("Unknown error", viewModel.vehicleListVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

    @Test
    fun givenServerResponse200_whenRetrievePaymentFetch_shouldReturnSuccess() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val retrievePaymentResp = Mockito.mock(RetrievePaymentListApiResponse::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        Mockito.`when`(resp.isSuccessful()).thenReturn(true)
        Mockito.`when`(resp.code()).thenReturn(200)
        Mockito.`when`(resp.body()).thenReturn(retrievePaymentResp)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).retrievePaymentList(accessToken ,requestParam )
            viewModel.retrievePaymentListApi(accessToken , requestParam)
            viewModel.paymentListVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.paymentListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(200, viewModel.paymentListVal.value!!.data!!.code())
            TestCase.assertEquals(true, viewModel.paymentListVal.value!!.data!!.isSuccessful)
            TestCase.assertEquals(
                retrievePaymentResp,
                viewModel.paymentListVal.value!!.data!!.body()
            )

//            Mockito.verify(apiHelper).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
//            viewModel.accountOverviewVal.removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseUnknownError_whenRetrievePaymentFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(4201)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).retrievePaymentList(accessToken, requestParam)
            viewModel.retrievePaymentListApi(accessToken, requestParam)
            viewModel.paymentListVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.paymentListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.paymentListVal.value!!.data)
            TestCase.assertEquals("Unknown error", viewModel.paymentListVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }



    @Test
    fun givenServerResponseError_whenRetrievePaymentFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(401)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).retrievePaymentList(accessToken, requestParam)
            viewModel.retrievePaymentListApi(accessToken, requestParam)
            viewModel.paymentListVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.paymentListVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.paymentListVal.value!!.data)
            TestCase.assertEquals("Invalid token", viewModel.paymentListVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

    @Test
    fun givenServerResponse200_whenMonthlyUsageFetch_shouldReturnSuccess() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val monthlyUsageResp = Mockito.mock(RetrievePaymentListApiResponse::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        val apiResp = Resource.success(monthlyUsageApiResponse)
        Mockito.`when`(resp.isSuccessful()).thenReturn(true)
        Mockito.`when`(resp.code()).thenReturn(200)
        Mockito.`when`(resp.body()).thenReturn(monthlyUsageResp)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getMonthlyUsageApiCall(accessToken ,requestParam )
            viewModel.getMonthlyUsage(accessToken , requestParam)
            viewModel.monthlyUsageVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.monthlyUsageVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(200, viewModel.monthlyUsageVal.value!!.data!!.code())
            TestCase.assertEquals(true, viewModel.monthlyUsageVal.value!!.data!!.isSuccessful)
            TestCase.assertEquals(
                monthlyUsageResp,
                viewModel.monthlyUsageVal.value!!.data!!.body()
            )

             Mockito.verify(apiHelper).getMonthlyUsageApiCall(accessToken , requestParam)
          //   Mockito.verify(apiRetrievePaymentObserver).onChanged(apiResp)
//            viewModel.accountOverviewVal.removeObserver(apiUsersObserver)
        }
    }



    @Test
    fun givenServerResponseError_whenMonthlyUsageFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(401)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getMonthlyUsageApiCall(accessToken, requestParam)
            viewModel.getMonthlyUsage(accessToken, requestParam)
            viewModel.monthlyUsageVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.monthlyUsageVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.monthlyUsageVal.value!!.data)
            TestCase.assertEquals("Invalid token", viewModel.monthlyUsageVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }


    @Test
    fun givenServerResponseUnknownError_whenMonthlyUsageFetch_shouldReturnError() {
        val viewModel = DashboardViewModel(apiHelper)
        val accessToken = "shgfjsgfhdgj"
        val resp = Mockito.mock(Response::class.java)
        val requestParam = RetrievePaymentListRequest(
            "Posted Date" ,
            "",
            "",
            "PREPAID",
            "TX_DATE",
            0,10,"",
            ""
        )
        Mockito.`when`(resp.isSuccessful()).thenReturn(false)
        Mockito.`when`(resp.code()).thenReturn(1234)
//        Mockito.`when`(resp.body()).thenReturn(null)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(resp).`when`(apiHelper).getMonthlyUsageApiCall(accessToken, requestParam)
            viewModel.getMonthlyUsage(accessToken, requestParam)
            viewModel.monthlyUsageVal.observeForever(apiRetrievePaymentObserver)
            delay(2000)
            TestCase.assertTrue(viewModel.monthlyUsageVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            TestCase.assertEquals(null, viewModel.monthlyUsageVal.value!!.data)
            TestCase.assertEquals("Unknown error", viewModel.monthlyUsageVal.value!!.message)

//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            Mockito.doThrow(RuntimeException(errorMessage))
//                .`when`(appRepository)
//                .getAccountOverviewApiCall(accessToken)
//            val viewModel = DashboardViewModel(appRepository)
//            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
//            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
//        }
        }
    }

}