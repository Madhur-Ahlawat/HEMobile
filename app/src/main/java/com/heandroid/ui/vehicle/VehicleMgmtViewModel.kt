package com.heandroid.ui.vehicle

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class VehicleMgmtViewModel @Inject constructor(private val repository: VehicleRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addVehicleApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val addVehicleApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _addVehicleApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateVehicleApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateVehicleApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _updateVehicleApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _deleteVehicleApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val deleteVehicleApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _deleteVehicleApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _crossingHistoryVal = MutableLiveData<Resource<CrossingHistoryApiResponse?>?>()
    val crossingHistoryVal: LiveData<Resource<CrossingHistoryApiResponse?>?> get() = _crossingHistoryVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _crossingHistoryDownloadVal = MutableLiveData<Resource<ResponseBody?>?>()
    val crossingHistoryDownloadVal: LiveData<Resource<ResponseBody?>?> get() = _crossingHistoryDownloadVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _vehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val vehicleListVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _vehicleListVal

    fun addVehicleApi(request: VehicleResponse?) {
        viewModelScope.launch {
            try {
                _addVehicleApiVal.postValue(
                    ResponseHandler.success(
                        repository.addVehicleApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _addVehicleApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun updateVehicleApi(request: VehicleResponse) {
        viewModelScope.launch {
            try {
                _updateVehicleApiVal.postValue(
                    ResponseHandler.success(
                        repository.updateVehicleApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _updateVehicleApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun crossingHistoryApiCall(request: CrossingHistoryRequest) {
        viewModelScope.launch {
            try {
                _crossingHistoryVal.postValue(
                    ResponseHandler.success(
                        repository.crossingHistoryApiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _crossingHistoryVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun downloadCrossingHistoryApiCall(request: TransactionHistoryDownloadRequest) {
        viewModelScope.launch {
            try {
                _crossingHistoryDownloadVal.postValue(
                    ResponseHandler.success(
                        repository.downloadCrossingHistoryAPiCall(request),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _crossingHistoryDownloadVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getVehicleInformationApi() {
        viewModelScope.launch {
            try {
                _vehicleListVal.postValue(
                    ResponseHandler.success(
                        repository.getVehicleListApiCall(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _vehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }
    fun deleteVehicleApi(deleteVehicleRequest : DeleteVehicleRequest) {
        viewModelScope.launch {
            try {
                _deleteVehicleApiVal.postValue(
                    ResponseHandler.success(
                        repository.deleteVehicleListApiCall(deleteVehicleRequest),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _deleteVehicleApiVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

}