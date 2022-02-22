package com.heandroid.utils

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.LoginResponse
import com.heandroid.oldStructure.network.ApiHelper
import com.heandroid.utils.common.Resource
import com.heandroid.viewmodel.LoginViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


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

    @Mock
    private lateinit var mockContext: Context

//    //Annotation for marking a field as Mock and here we mocked the Response class
//    @Mock
//    private lateinit var loginModel : Response<LoginResponse>


    //Creating mock for the observer
    private val mockObserverForStates = mock<Observer<Response<LoginResponse>>>()

    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

    @Before
    fun setUp() {
        // do something if required
        MockitoAnnotations.initMocks(this)
         viewModel = LoginViewModel(apiHelper)


    }

    @Test
    fun `test login for success`() {
        val clientID = "NY_EZ_Pass_iOS_QA"
        val grantType = "password"
        val agencyId = "12"
        val clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        val value = "johnsmith32"
        val password = "Welcome1!"
        val validatePasswordCompliance = "true"
        val loginModel1 = Mockito.mock(Response::class.java)
        val loginResponse = Mockito.mock(LoginResponse::class.java)
        `when`(loginModel1.isSuccessful()).thenReturn(true)
        `when`(loginModel1.code()).thenReturn(200)
        `when`(loginModel1.body()).thenReturn(loginResponse)

        testCoroutineRule.runBlockingTest {
            doReturn(loginModel1)
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)
            viewModel.loginUser(clientID,
                grantType,
                agencyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.loginUserVal.value is Resource)
           // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            assertEquals(200 , viewModel.loginUserVal.value!!.data!!.code())
            assertEquals(true , viewModel.loginUserVal.value!!.data!!.isSuccessful)
            assertEquals(loginResponse , viewModel.loginUserVal.value!!.data!!.body())


        }
    }

    @Test
    fun `test login for error for invalid credentials`() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = ""
        var password = ""
        var validatePasswordCompliance = "true"

        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(401)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)
            viewModel.loginUser(clientID,
                grantType,
                agencyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.loginUserVal.value is Resource)
            assertEquals(null , viewModel.loginUserVal.value!!.data)
            assertEquals("Invalid login credentials" , viewModel.loginUserVal.value!!.message)
        }
    }
    @Test
    fun `test login for unknown  error`() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"
        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(1234)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)
            viewModel.loginUser(clientID,
                grantType,
                agencyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.loginUserVal.value is Resource)
            assertEquals(null , viewModel.loginUserVal.value!!.data)
            assertEquals("Unknown error " , viewModel.loginUserVal.value!!.message)
        }
    }

    @Test
    fun `test login for error for Exception`() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"


        val errorMessage = "Error Message For You"
       //`when`(loginModel.isSuccessful()).thenReturn(false)
       // `when`(loginModel.code()).thenReturn(401)
        testCoroutineRule.runBlockingTest {
            doThrow(NullPointerException::class.java)
           // doThrow(RuntimeException(errorMessage))
                .`when`(apiHelper)
                .loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)
            viewModel.loginUser(clientID,
                grantType,
                agencyId,
                clientSecret,
                value,
                password,
                validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.loginUserVal.value is Resource)
            assertEquals(null , viewModel.loginUserVal.value!!.data)
            assertEquals("java.lang.NullPointerException" , viewModel.loginUserVal.value!!.message)
            verify(apiHelper).loginApiCall(clientID, grantType, agencyId, clientSecret, value, password,validatePasswordCompliance )
        }
    }

    @Test
    fun `test RenewalAccessToken for success`() {
        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = "ggdfhhfgjh"
        var validatePasswordCompliance =  "true"
        val loginModel1 = Mockito.mock(Response::class.java)
        val loginResponse = Mockito.mock(LoginResponse::class.java)
        `when`(loginModel1.isSuccessful()).thenReturn(true)
        `when`(loginModel1.code()).thenReturn(200)
        `when`(loginModel1.body()).thenReturn(loginResponse)

        testCoroutineRule.runBlockingTest {
            doReturn(loginModel1)
                .`when`(apiHelper)
                .getRenewalAccessToken(
                    clientId,
                    grantType,
                    agencyId,
                    clientSecret,
                    refreshToken,
                    validatePasswordCompliance)
            viewModel.getRenewalAccessToken( clientId,
                grantType,
                agencyId,
                clientSecret,
                refreshToken,
                validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.renewalUserLoginVal.value is Resource)
            // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            assertEquals(200 , viewModel.renewalUserLoginVal.value!!.data!!.code())
            assertEquals(true , viewModel.renewalUserLoginVal.value!!.data!!.isSuccessful)
            assertEquals(loginResponse , viewModel.renewalUserLoginVal.value!!.data!!.body())


        }
    }

    @Test
    fun `test RenewalAccessToken for invalid credentials`() {
        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = "ggdfhhfgjh"
        var validatePasswordCompliance =  "true"

        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(401)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(apiHelper)
                .getRenewalAccessToken(
                    clientId ,grantType,agencyId,clientSecret,
            refreshToken, validatePasswordCompliance)
            viewModel.getRenewalAccessToken(clientId ,grantType,agencyId,clientSecret,
                refreshToken, validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.renewalUserLoginVal.value is Resource)
            assertEquals(null , viewModel.renewalUserLoginVal.value!!.data)
            assertEquals("Invalid login credentials" , viewModel.renewalUserLoginVal.value!!.message)
        }
    }
    @Test
    fun `test RenewalAccessToken for unknown  error`() {
        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = "ggdfhhfgjh"
        var validatePasswordCompliance =  "true"
        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(1234)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(apiHelper)
                .getRenewalAccessToken(clientId ,grantType,agencyId,clientSecret,
                    refreshToken, validatePasswordCompliance)
            viewModel.getRenewalAccessToken(clientId ,grantType,agencyId,clientSecret,
                refreshToken, validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.renewalUserLoginVal.value is Resource)
            assertEquals(null , viewModel.renewalUserLoginVal.value!!.data)
            assertEquals("Unknown error " , viewModel.renewalUserLoginVal.value!!.message)
        }
    }

    @Test
    fun `test RenewalAccessToken for Exception`() {
        var clientId = "NY_EZ_Pass_iOS_QA"
        var grantType = "refresh_token"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var refreshToken = "ggdfhhfgjh"
        var validatePasswordCompliance =  "true"


        val errorMessage = "Error Message For You"
        //`when`(loginModel.isSuccessful()).thenReturn(false)
        // `when`(loginModel.code()).thenReturn(401)
        testCoroutineRule.runBlockingTest {
            doThrow(NullPointerException::class.java)
                // doThrow(RuntimeException(errorMessage))
                .`when`(apiHelper)
                .getRenewalAccessToken(clientId,
                    grantType,
                    agencyId,
                    clientSecret,
                    refreshToken,validatePasswordCompliance)
            viewModel.getRenewalAccessToken(clientId,
                grantType,
                agencyId,
                clientSecret,
                refreshToken,validatePasswordCompliance)
            delay(2000)
            assertTrue(viewModel.renewalUserLoginVal.value is Resource)
            assertEquals(null , viewModel.renewalUserLoginVal.value!!.data)
            assertEquals("java.lang.NullPointerException" , viewModel.renewalUserLoginVal.value!!.message)
            verify(apiHelper).getRenewalAccessToken(clientId, grantType, agencyId, clientSecret, refreshToken,validatePasswordCompliance )
        }
    }

    @After
    fun tearDown() {
        // do something if required
    }

}


