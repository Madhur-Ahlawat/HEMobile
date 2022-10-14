package com.conduent.nationalhighways.data.repository.vehicle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.conduent.nationalhighways.data.remote.ApiService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class VehicleRepositoryTest {

    private var vehicleRepository: VehicleRepository? = null

    @Mock
    private lateinit var apiService: ApiService

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        vehicleRepository = VehicleRepository(apiService)
    }

    @Test
    fun `test add vehicle api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.addVehicleApi( null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.addVehicleApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test update vehicle api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateVehicleApi(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.updateVehicleApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test get vehicle crossing history data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehicleCrossingHistoryData(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.crossingHistoryApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test download transaction list api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getDownloadTransactionListDataInFile(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.downloadCrossingHistoryAPiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test delete vehicle api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.deleteVehicle(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.deleteVehicleListApiCall(null), null
                )
            }
        }
    }
    @Test
    fun `test get Vehicle data api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehicleData("0","0")
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getVehicleListApiCall(), null
                )
            }
        }
    }
    @Test
    fun `test get Vehicle group list api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehicleGroupList()
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getVehicleGroupListApiCall(), null
                )
            }
        }
    }

    @Test
    fun `test add vehicle group api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.addVehicleGroup(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.addVehicleGroupApiCall(null), null
                )
            }
        }
    }
    @Test
    fun `test remove vehicle group api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.renameVehicleGroup(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.renameVehicleGroupApiCall(null), null
                )
            }
        }
    }
    @Test
    fun `test delete vehicle group api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.deleteVehicleGroup(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.deleteVehicleGroupApiCall(null), null
                )
            }
        }
    }

    @Test
    fun `test get vehicle list of group api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getVehiclesListOfGroup("")
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getVehicleListOfGroupApiCall(""), null
                )
            }
        }
    }
    @Test
    fun `test get search vehicle list for group api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getSearchVehiclesForGroup("","")
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getSearchVehicleForGroupApiCall("",""), null
                )
            }
        }
    }
    @Test
    fun `test get download vehicle list  api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getDownloadVehicleList("")
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getDownloadVehicleList(""), null
                )
            }
        }
    }
    @Test
    fun `test update vehicle list management  api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.updateVehicleListManagement(null)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.updateVehicleListManagement(null), null
                )
            }
        }
    }
    @Test
    fun `test get account find vehicle api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAccountFindVehicle("",0)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.getVehicleDetail("",0), null
                )
            }
        }
    }
    @Test
    fun `test valid vehicle check api call for null`() {
        runTest {
            Mockito.`when`(
                apiService.validVehicleCheck(null,0)
            ).thenReturn(null)
            vehicleRepository?.let {
                assertEquals(
                    it.validVehicleCheck(null,0), null
                )
            }
        }
    }
}