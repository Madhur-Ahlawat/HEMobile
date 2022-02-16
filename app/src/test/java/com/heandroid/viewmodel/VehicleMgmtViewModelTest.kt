package com.heandroid.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class VehicleMgmtViewModelTest {

    private var vehicleMgmtViewModel: VehicleMgmtViewModel? = null
    private val vehicleRequest: VehicleResponse =
        VehicleResponse(PlateInfoResponse(), PlateInfoResponse(), VehicleInfoResponse(), false)

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var response: Response<EmptyApiResponse>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        vehicleMgmtViewModel = VehicleMgmtViewModel(apiHelper)
    }

    @Test
    fun `test add vehicle api call for success`() {
        runBlockingTest {
            val status = 200
            val message = "success"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.success(response), it.addVehicleApiVal.value
                )
                assertEquals(
                    response, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    status, it.addVehicleApiVal.value?.data?.body()?.status
                )
                assertEquals(
                    message, it.addVehicleApiVal.value?.data?.body()?.message
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for invalid token error`() {
        runBlockingTest {
            val status = 401
            val message = "Invalid token"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(response.code()).thenReturn(status)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.error(null, message), it.addVehicleApiVal.value
                )
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.addVehicleApiVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test add vehicle api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(response.code()).thenReturn(status)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.addVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.addVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.error(null, message), it.addVehicleApiVal.value
                )
                assertEquals(
                    null, it.addVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.addVehicleApiVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for success`() {
        runBlockingTest {
            val status = 200
            val message = "success"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(true)
            Mockito.lenient().`when`(response.code()).thenReturn(200)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.success(response), it.updateVehicleApiVal.value
                )
                assertEquals(
                    response, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    status, it.updateVehicleApiVal.value?.data?.body()?.status
                )
                assertEquals(
                    message, it.updateVehicleApiVal.value?.data?.body()?.message
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for invalid token error`() {
        runBlockingTest {
            val status = 401
            val message = "Invalid token"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(response.code()).thenReturn(status)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.error(null, message), it.updateVehicleApiVal.value
                )
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.updateVehicleApiVal.value?.message
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for unknown error`() {
        runBlockingTest {
            val status = 403
            val message = "Unknown error"
            Mockito.lenient().`when`(response.isSuccessful).thenReturn(false)
            Mockito.lenient().`when`(response.code()).thenReturn(status)
            Mockito.lenient().`when`(response.body()).thenReturn(EmptyApiResponse(status, message))
            Mockito.`when`(apiHelper.updateVehicleApiCall(vehicleRequest)).thenReturn(response)
            vehicleMgmtViewModel?.let {
                it.updateVehicleApi(vehicleRequest)
                assertEquals(
                    Resource.error(null, message), it.updateVehicleApiVal.value
                )
                assertEquals(
                    null, it.updateVehicleApiVal.value?.data
                )
                assertEquals(
                    message, it.updateVehicleApiVal.value?.message
                )
            }
        }
    }


}