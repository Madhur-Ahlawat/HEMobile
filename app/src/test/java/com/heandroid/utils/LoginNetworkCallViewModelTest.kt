package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.Observer
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.viewmodel.LoginViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginNetworkCallViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var apiUsersObserver: Observer<Resource<Response<LoginResponse>>>

    @Mock
    private lateinit var apiloginresponse: Response<LoginResponse>

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        // do something if required
         viewModel = LoginViewModel(apiHelper)
    }

    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agecyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"
        testCoroutineRule.runBlockingTest {
            doReturn(emptyList<LoginResponse>())
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agecyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)

                viewModel.loginUserVal.observeForever(apiUsersObserver)
                verify(apiHelper).loginApiCall(clientID,
                grantType,
                agecyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
              verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
                viewModel.loginUserVal.removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agecyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "WrongUsername"
        var password = "Wrong Password"
        var validatePasswordCompliance = "true"
        testCoroutineRule.runBlockingTest {
            val errorMessage = "Error Message For You"
            doThrow(RuntimeException(errorMessage))
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agecyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)

            viewModel.loginUserVal.observeForever(apiUsersObserver)
            verify(apiHelper).loginApiCall(clientID,
                grantType,
                agecyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
            verify(apiUsersObserver).onChanged(
                Resource.error(null,
                    RuntimeException(errorMessage).toString()
                )
            )
            viewModel.loginUserVal.removeObserver(apiUsersObserver)
        }
    }


   /* @Test
    fun renewalToken200_whenFetch_shouldReturnSuccess() {

        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = sessionManager.fetchRefreshToken()
        var validatePasswordCompliance = "true"
        testCoroutineRule.runBlockingTest {
            doReturn(emptyList<LoginResponse>())
                .`when`(apiHelper)
                .getRenewalAccessToken(clientId, grantType, agencyId, clientSecret,
                    refreshToken, validatePasswordCompliance)
            val viewModel = LoginViewModel(apiHelper)
            viewModel.getRenewalAccessToken(clientId, grantType, agencyId, clientSecret,
                refreshToken, validatePasswordCompliance).observeForever(apiUsersObserver)
            verify(apiHelper).getRenewalAccessToken(clientId, grantType, agencyId, clientSecret,
                refreshToken, validatePasswordCompliance)
            verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
            viewModel.getRenewalAccessToken(clientId, grantType, agencyId, clientSecret,
                refreshToken, validatePasswordCompliance).removeObserver(apiUsersObserver)
        }
    }*/

    @After
    fun tearDown() {
        // do something if required
    }

}


