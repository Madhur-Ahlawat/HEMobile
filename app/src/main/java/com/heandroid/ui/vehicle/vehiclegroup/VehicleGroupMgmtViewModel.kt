package com.heandroid.ui.vehicle.vehiclegroup

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.vehicle.*
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleGroupMgmtViewModel @Inject constructor(
    private val repository: VehicleRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getVehicleGroupListApiVal =
        MutableLiveData<Resource<List<VehicleGroupResponse?>?>?>()
    val getVehicleGroupListApiVal: LiveData<Resource<List<VehicleGroupResponse?>?>?> get() = _getVehicleGroupListApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addVehicleGroupApiVal = MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()
    val addVehicleGroupApiVal: LiveData<Resource<VehicleGroupMngmtResponse?>?> get() = _addVehicleGroupApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _renameVehicleGroupApiVal = MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()
    val renameVehicleGroupApiVal: LiveData<Resource<VehicleGroupMngmtResponse?>?> get() = _renameVehicleGroupApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _deleteVehicleGroupApiVal = MutableLiveData<Resource<VehicleGroupMngmtResponse?>?>()
    val deleteVehicleGroupApiVal: LiveData<Resource<VehicleGroupMngmtResponse?>?> get() = _deleteVehicleGroupApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val vehicleListVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _vehicleListVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _searchVehicleVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val searchVehicleVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _searchVehicleVal

    fun getVehicleGroupListApi() {
        viewModelScope.launch {
            try {
                _getVehicleGroupListApiVal.postValue(
                    ResponseHandler.success(
                        repository.getVehicleGroupListApiCall(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _getVehicleGroupListApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun addVehicleGroupApi(request: AddDeleteVehicleGroup?) {
        viewModelScope.launch {
            try {
                _addVehicleGroupApiVal.postValue(
                    ResponseHandler.success(
                        repository.addVehicleGroupApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _addVehicleGroupApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun renameVehicleGroupApi(request: RenameVehicleGroup) {
        viewModelScope.launch {
            try {
                _renameVehicleGroupApiVal.postValue(
                    ResponseHandler.success(
                        repository.renameVehicleGroupApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _renameVehicleGroupApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun deleteVehicleGroupApi(deleteVehicleGroupRequest: AddDeleteVehicleGroup) {
        viewModelScope.launch {
            try {
                _deleteVehicleGroupApiVal.postValue(
                    ResponseHandler.success(
                        repository.deleteVehicleGroupApiCall(deleteVehicleGroupRequest),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _deleteVehicleGroupApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getVehiclesOfGroupApi(vehicleGroup: VehicleGroupResponse) {
        viewModelScope.launch {
            try {
                _vehicleListVal.postValue(
                    ResponseHandler.success(
                        repository.getVehicleListOfGroupApiCall(vehicleGroup.groupName),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getSearchVehiclesForGroup(vehicleGroup: String, plateNumber: String) {
        viewModelScope.launch {
            try {
                _searchVehicleVal.postValue(
                    ResponseHandler.success(
                        repository.getSearchVehicleForGroupApiCall(vehicleGroup, plateNumber),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _searchVehicleVal.postValue(ResponseHandler.failure(e))
            }
        }
    }
}