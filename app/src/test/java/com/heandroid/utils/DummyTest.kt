package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.viewmodel.DummyTestViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DummyTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var apiUsersObserver:Observer<Resource<Response<AccountResponse>>>

    @Before
    fun setup(){
    }

    @Test
    fun givenApiSuccess()
    {
        testCoroutineRule.runBlockingTest {
            doReturn(emptyList<AccountResponse>())
                .`when`(apiHelper)
                .getAccountOverviewApiCall("abcd")
            val viewModel = DummyTestViewModel(apiHelper)
            viewModel.getAccountOverView().observeForever(apiUsersObserver)
            verify(apiHelper).getAccountOverviewApiCall("abcd")
            viewModel.getAccountOverView().removeObserver(apiUsersObserver)
        }
    }
    @After
    fun tearDown()
    {

    }

}
