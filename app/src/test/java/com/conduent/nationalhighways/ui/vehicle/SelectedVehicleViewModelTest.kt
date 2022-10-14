package com.conduent.nationalhighways.ui.vehicle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
@MediumTest
class SelectedVehicleViewModelTest {

    private var selectedVehicleViewModel: SelectedVehicleViewModel? = null
    private val vehicleData: VehicleResponse =
        VehicleResponse(PlateInfoResponse(), PlateInfoResponse(), VehicleInfoResponse(), false)

    @Inject
    lateinit var errorManager: ErrorManager

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        selectedVehicleViewModel = SelectedVehicleViewModel(errorManager)
    }

    @Test
    fun `test case`() {
        assertEquals(
            "hello", "hello"
        )
    }

    @Test
    fun `test welcome viewModel`() {
        selectedVehicleViewModel?.setSelectedVehicleResponse(vehicleData)
        selectedVehicleViewModel?.let {
            assertEquals(
                it.selectedVehicleResponse.value,
                vehicleData
            )
        }
    }
}