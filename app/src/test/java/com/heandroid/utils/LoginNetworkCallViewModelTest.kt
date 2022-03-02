package com.heandroid.utils

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.ui.auth.login.LoginViewModel
import com.heandroid.utils.common.Resource
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
import java.lang.RuntimeException


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginNetworkCallViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var loginRepository: LoginRepository

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
         viewModel = LoginViewModel(loginRepository)


    }

    @Test
    fun `test login for success`() {

        val value = "100312803"
        val password = "Welcome1"
        val validatePasswordCompliance = "true"
        val loginModel1 = Mockito.mock(Response::class.java)
        val loginResponse = Mockito.mock(LoginResponse::class.java)
        `when`(loginModel1.isSuccessful).thenReturn(true)
        `when`(loginModel1.code()).thenReturn(200)
        `when`(loginModel1.body()).thenReturn(loginResponse)

        testCoroutineRule.runBlockingTest {
            doReturn(loginModel1)
                .`when`(loginRepository)
                .login(LoginModel(value, password, validatePasswordCompliance = "true"))
            viewModel.login(LoginModel(value, password, validatePasswordCompliance = "true"))
            delay(2000)
            assertTrue(viewModel.login.value is Resource)
           // assertTrue(viewModel.loginUserVal.value!!.data is Response<LoginResponse>)
            assertEquals(loginResponse , viewModel.login.value!!.data)


        }
    }

    @Test
    fun `test login for error for invalid credentials`() {
        val value = "100312803"
        val password = "Welcome1"
        val validatePasswordCompliance = "true"

        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(401)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(loginRepository)
                .login(LoginModel(value, password, validatePasswordCompliance = "true"))
            viewModel.login(LoginModel(value, password, validatePasswordCompliance = "true"))
            delay(2000)
            assertTrue(viewModel.login.value is Resource)
            assertEquals(null , viewModel.login.value!!.data)
            assertEquals("Invalid login credentials" , viewModel.login.value!!.errorMsg)
        }
    }
    @Test
    fun `test login for unknown  error`() {
        val value = "100312803"
        val password = "Welcome1"
        val validatePasswordCompliance = "true"
        val loginModel = Mockito.mock(Response::class.java)
        `when`(loginModel.isSuccessful()).thenReturn(false)
        `when`(loginModel.code()).thenReturn(1234)
        testCoroutineRule.runBlockingTest {
            doReturn(loginModel)
                .`when`(loginRepository)
                .login(LoginModel(value, password, validatePasswordCompliance = "true"))
            viewModel.login(LoginModel(value, password, validatePasswordCompliance = "true"))
            delay(2000)
            assertTrue(viewModel.login.value is Resource)
            assertEquals(null , viewModel.login.value!!.data)
            assertEquals("Unknown error " , viewModel.login.value!!.errorMsg)
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
            doReturn(RuntimeException(errorMessage))
                .`when`(loginRepository)
                .login(LoginModel(value, password, validatePasswordCompliance = "true"))
            viewModel.login(LoginModel(value, password, validatePasswordCompliance = "true"))
            delay(2000)
            assertTrue(viewModel.login.value is Resource)
            assertEquals(null , viewModel.login.value!!.data)
            assertEquals("java.lang.NullPointerException" , viewModel.login.value!!.errorMsg)
            verify(loginRepository).login(LoginModel(value, password, validatePasswordCompliance = "true"))
        }
    }


    @After
    fun tearDown() {
        // do something if required
    }

}


