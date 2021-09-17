package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.viewmodel.DashboardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var appRepository: ApiHelper

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
        var accessToken = "hgdcgdhc"
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(emptyList<AccountResponse>())
                .`when`(appRepository)
                .getAccountOverviewApiCall(accessToken)
            val viewModel = DashboardViewModel(appRepository)
            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
//            Mockito.verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
//            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenAccountOverViewFetch_shouldReturnError() {
        var accessToken = "gchegcjhedgcjh"
        testCoroutineRule.runBlockingTest {
            val errorMessage = "Error Message For You"
            Mockito.doThrow(RuntimeException(errorMessage))
                .`when`(appRepository)
                .getAccountOverviewApiCall(accessToken)
            val viewModel = DashboardViewModel(appRepository)
            viewModel.getAccountOverViewApi(accessToken).observeForever(apiUsersObserver)
            Mockito.verify(appRepository).getAccountOverviewApiCall(accessToken)
            Mockito.verify(apiUsersObserver).onChanged(
                Resource.error(
                    null,
                    RuntimeException(errorMessage).toString()
                )
            )
            viewModel.getAccountOverViewApi(accessToken).removeObserver(apiUsersObserver)
        }
    }
}