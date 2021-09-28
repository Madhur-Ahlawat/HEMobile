package com.heandroid.utils

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.view.LoginActivity
import com.heandroid.viewmodel.LoginViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import net.bytebuddy.matcher.ElementMatchers.`is`
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import org.junit.Rule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*


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
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"
        val loginModel1 = Mockito.mock(Response::class.java)
        //`when`(loginModel.code()).thenReturn(200)
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
            assertEquals(201 , viewModel.loginUserVal.value!!.data)

        }
    }

    @Test
    fun `test login for error for invalid credentials`() {
        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "johnsmith32"
        var password = "Welcome1!"
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
    fun `test login for unknow  error`() {
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

    @After
    fun tearDown() {
        // do something if required
    }

}


