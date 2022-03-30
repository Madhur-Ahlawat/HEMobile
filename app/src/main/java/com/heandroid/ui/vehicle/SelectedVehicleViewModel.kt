package com.heandroid.ui.vehicle

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedVehicleViewModel @Inject constructor(private val repository: VehicleRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _selectedVehicleResponse = MutableLiveData<VehicleResponse?>()
    val selectedVehicleResponse: LiveData<VehicleResponse?> get() = _selectedVehicleResponse

    fun setSelectedVehicleResponse(details: VehicleResponse?) {
        _selectedVehicleResponse.value= details
    }

}