package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
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
    private lateinit var apiloginresponse: Response<AccountResponse>

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
        Mockito.`when`(resp.body()).thenReturn(null)
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
}