package com.heandroid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountInformation
import com.heandroid.model.AccountResponse
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.AppRepository
import com.heandroid.repo.Resource
import com.heandroid.utils.TestCoroutineRule
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var appRepository: AppRepository

    @Mock
    private lateinit var accountOverviewApiObserver: Observer<Resource<Response<AccountResponse>>>

    @Mock
    private lateinit var accountApiResponse: Response<AccountResponse>

    @Test
    fun givenServerResponse200_whenFetchAccountOverview_shouldReturnSuccess() {

        var authToken = "hgjhgh"
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(emptyList<AccountResponse>())
                .`when`(appRepository)
                .getAccountOverviewApiCall(authToken)
            val viewModel = DashboardViewModel(appRepository)
            viewModel.getAccountOverViewApi(authToken).observeForever(accountOverviewApiObserver)
            Mockito.verify(appRepository).getAccountOverviewApiCall(authToken)
            Mockito.verify(accountOverviewApiObserver).onChanged(Resource.success(accountApiResponse))
            viewModel.getAccountOverViewApi(authToken).removeObserver(accountOverviewApiObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetchAccountOverview_shouldReturnError() {
        var authToken = "hgjhgh"
        testCoroutineRule.runBlockingTest {
            val errorMessage = "Error Message For You"
            Mockito.doThrow(RuntimeException(errorMessage))
                .`when`(appRepository)
                .getAccountOverviewApiCall(authToken)
            val viewModel = DashboardViewModel(appRepository)
            viewModel.getAccountOverViewApi(authToken).observeForever(accountOverviewApiObserver)
            Mockito.verify(appRepository).getAccountOverviewApiCall(authToken)
            Mockito.verify(accountOverviewApiObserver).onChanged(
                Resource.error(null,
                    RuntimeException(errorMessage).toString()
                )
            )
            viewModel.getAccountOverViewApi(authToken).removeObserver(accountOverviewApiObserver)
        }
    }

}