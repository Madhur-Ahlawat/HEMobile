//package com.heandroid.utils
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.Observer
//import com.heandroid.model.LoginResponse
//import com.heandroid.model.VehicleResponse
//import com.heandroid.network.ApiHelper
//import com.heandroid.repo.Resource
//import com.heandroid.viewmodel.DashboardViewModel
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.rules.TestRule
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnitRunner
//import retrofit2.Response
//
//@ExperimentalCoroutinesApi
//@RunWith(MockitoJUnitRunner::class)
//class VehicleInfoNetworkCallViewModelTest {
//
//    @get:Rule
//    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val testCoroutineRule = TestCoroutineRule()
//
//    @Mock
//    private lateinit var apiHelper: ApiHelper
//
//    @Mock
//    private lateinit var apiUsersObserver: Observer<Resource<Response<List<VehicleResponse>>>>
//
//    @Mock
//    private lateinit var apiloginresponse: Response<List<VehicleResponse>>
//
//    @Before
//    fun setUp() {
//        // do something if required
//    }
//
//   // @Test
//    fun vehicleResponse_200_ReturnSuccess() {
//
//        testCoroutineRule.runBlockingTest {
//            val authToken = ""
//            doReturn(emptyList<LoginResponse>())
//                .`when`(apiHelper)
//                .getVehicleListApiCall()
//            val viewModel = DashboardViewModel(apiHelper)
//            viewModel.getVehicleInformationApi()
//            verify(apiHelper).getVehicleListApiCall()
//            verify(apiUsersObserver).onChanged(Resource.success(apiloginresponse))
//            //viewModel.getVehicleInformationApi(authToken).removeObserver(apiUsersObserver)
//        }
//    }
//
//   // @Test
//    fun vehicleResponse_ReturnError() {
//        testCoroutineRule.runBlockingTest {
//            val errorMessage = "Error Message For You"
//            val authToken = ""
//            doThrow(RuntimeException(errorMessage))
//                .`when`(apiHelper)
//                .getVehicleListApiCall()
//            val viewModel = DashboardViewModel(apiHelper)
//           // viewModel.getVehicleInformationApi(authToken).observeForever(apiUsersObserver)
//            verify(apiHelper).getVehicleListApiCall()
//            verify(apiUsersObserver).onChanged(
//                Resource.error(
//                    null,
//                    RuntimeException(errorMessage).toString()
//                )
//            )
//           // viewModel.getVehicleInformationApi(authToken).removeObserver(apiUsersObserver)
//        }
//    }
//
//
//    @After
//    fun tearDown() {
//        // do something if required
//    }
//
//}
//
//
//
