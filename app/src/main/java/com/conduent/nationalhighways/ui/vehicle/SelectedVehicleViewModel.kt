package com.conduent.nationalhighways.ui.vehicle

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedVehicleViewModel @Inject constructor(
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _selectedVehicleResponse = MutableLiveData<VehicleResponse?>()
    val selectedVehicleResponse: LiveData<VehicleResponse?> get() = _selectedVehicleResponse

    fun setSelectedVehicleResponse(details: VehicleResponse?) {
        _selectedVehicleResponse.value = details
    }

}