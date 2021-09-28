package com.heandroid.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heandroid.model.AccountResponse
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import com.heandroid.viewmodel.DummyTestViewModel
import com.heandroid.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.api.mockito.PowerMockito.mockStatic
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

    //Annotation for marking a field as Mock and here we mocked the Response class
    @Mock
    private lateinit var loginModel : LoginResponse

    //Creating mock for the observer
    private val mockObserverForStates = mock<Observer<LoginDataState>>()

    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

    //Class to be tested
    private lateinit var loginViewModel: DummyTestViewModel


    //    //A JUnit Test Rule that swaps the background executor used by the Architecture Components with a
//    // different one which executes each task synchronously.
    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    //
//    //  This class is a unit test rule which overrides the default Dispatchers.Main dispatcher and replaces the default with our test dispatcher.
    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    @Before
    fun setup(){
        // Enable static mocking for all methods of a class.
       mockStatic(UtilityClass::class.java)

        // Initializing the class to be tested
        loginViewModel = DummyTestViewModel(apiHelper)
        loginViewModel.getObserverState().observeForever(mockObserverForStates)
        println("Before Class")
    }


    @Test
    fun testIfEmailAndPasswordValid_DoLogin() {
// Arrange
        Mockito.`when`(UtilityClass.isEmailValid(ArgumentMatchers.anyString())).thenAnswer { true }
        Mockito.`when`(UtilityClass.isPasswordValid(ArgumentMatchers.anyString())).thenAnswer { true }

        var clientID = "NY_EZ_Pass_iOS_QA"
        var grantType = "password"
        var agencyId = "12"
        var clientSecret = "N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
        var value = "459144698"
        var password = "Welcome1!"
        var validatePasswordCompliance = "true"

//        //TO test suspend functions in junit, we use **runBlockingTest**, for normal functions this is not needed.
        runBlockingTest {
            Mockito.`when`(apiHelper
                .loginApiCall(clientID,grantType,agencyId, clientSecret, value ,  password,validatePasswordCompliance ))
                .thenReturn(
                Response.success(loginModel)
            )
        }

//        //Act

        loginViewModel.authenticate(clientID,grantType,agencyId, clientSecret, value ,  password,validatePasswordCompliance )
//
//        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(
            mockObserverForStates, Mockito.times(2)
        ).onChanged(LoginDataState.Success(ArgumentMatchers.any()))
        Mockito.verifyNoMoreInteractions(mockObserverForStates)
    }

    @After
    fun tearDown()
    {
        println("After Class")
    }


}
