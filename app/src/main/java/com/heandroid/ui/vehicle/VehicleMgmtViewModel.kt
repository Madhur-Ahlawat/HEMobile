package com.heandroid.ui.vehicle

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.ErrorResponseModel
import com.heandroid.data.model.account.ValidVehicleCheckRequest
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.*
import com.heandroid.data.repository.vehicle.VehicleRepository
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class VehicleMgmtViewModel @Inject constructor(
    private val repository: VehicleRepository,
    val errorManager: ErrorManager
) :
    ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addVehicleApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val addVehicleApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _addVehicleApiVal

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateVehicleApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateVehicleApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _updateVehicleApiVal

    private val _removeVehiclesFromGroupApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val removeVehiclesFromGroupApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _removeVehiclesFromGroupApiVal

    private val _addVehiclesToGroupApiVal = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val addVehiclesToGroupApiVal: LiveData<Resource<EmptyApiResponse?>?> get() = _addVehiclesToGroupApiVal

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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _unAllocatedVehicleListVal = MutableLiveData<Resource<List<VehicleResponse?>?>?>()
    val unAllocatedVehicleListVal: LiveData<Resource<List<VehicleResponse?>?>?> get() = _unAllocatedVehicleListVal

    private val _vehicleVRMDownloadVal = MutableLiveData<Resource<ResponseBody?>?>()
    val vehicleVRMDownloadVal: LiveData<Resource<ResponseBody?>?> get() = _vehicleVRMDownloadVal

    private val _vehicleListManagementEditVal = MutableLiveData<Resource<String?>?>()
    val vehicleListManagementEditVal: LiveData<Resource<String?>?> get() = _vehicleListManagementEditVal

    private val findVehicleMutData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    val findVehicleLiveData: LiveData<Resource<VehicleInfoDetails?>?> get() = findVehicleMutData

    private val validVehicleMutData = MutableLiveData<Resource<String?>?>()
    val validVehicleLiveData: LiveData<Resource<String?>?> get() = validVehicleMutData

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

    fun removeVehiclesFromGroup(list: List<VehicleResponse?>) {
        var successCount = 0
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    coroutineScope {
                        list.forEach { vehicleGroup ->
                            delay(500)
                            launch {
                                vehicleGroup?.let {
                                    val request = it.apply {
                                        newPlateInfo = plateInfo
                                        vehicleInfo?.vehicleClassDesc =
                                            VehicleClassTypeConverter.toClassCode(vehicleInfo?.vehicleClassDesc)
                                        newPlateInfo?.vehicleGroup = ""
                                    }
                                    val apiResponse = repository.updateVehicleApiCall(request)
                                    if (apiResponse != null) {
                                        if (apiResponse.isSuccessful) {
                                            successCount++
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (successCount == list.size) {
                        _removeVehiclesFromGroupApiVal.postValue(
                            Resource.Success(EmptyApiResponse(200, "success"))
                        )
                    } else if (successCount == 0 && list.size == 1) {
                        _removeVehiclesFromGroupApiVal.postValue(
                            Resource.DataError("Failed to remove vehicle")
                        )
                    } else if (successCount == 0 && list.size > 1) {
                        _removeVehiclesFromGroupApiVal.postValue(
                            Resource.DataError("Failed to remove all vehicles")
                        )
                    } else if (successCount < list.size && list.size > 1) {
                        _removeVehiclesFromGroupApiVal.postValue(
                            Resource.DataError("Few vehicle(s) failed to remove.")
                        )
                    }
                } catch (e: Exception) {
                    _removeVehiclesFromGroupApiVal.postValue(ResponseHandler.failure(e))
                }
            }
        }
    }

    fun addVehiclesToGroup(list: List<VehicleResponse?>, vehicleGroupData: VehicleGroupResponse?) {
        var successCount = 0
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    coroutineScope {
                        list.forEach { vehicleGroup ->
                            delay(500)
                            launch {
                                vehicleGroup?.let {
                                    val request = it.apply {
                                        newPlateInfo = plateInfo
                                        vehicleInfo?.vehicleClassDesc =
                                            VehicleClassTypeConverter.toClassCode(vehicleInfo?.vehicleClassDesc)
                                        newPlateInfo?.vehicleGroup =
                                            vehicleGroupData?.groupName.toString()
                                    }
                                    val apiResponse = repository.updateVehicleApiCall(request)
                                    if (apiResponse != null) {
                                        if (apiResponse.isSuccessful) {
                                            successCount++
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (successCount == list.size) {
                        _addVehiclesToGroupApiVal.postValue(
                            Resource.Success(EmptyApiResponse(200, "success"))
                        )
                    } else if (successCount == 0 && list.size == 1) {
                        _addVehiclesToGroupApiVal.postValue(
                            Resource.DataError("Failed to add vehicle")
                        )
                    } else if (successCount == 0 && list.size > 1) {
                        _addVehiclesToGroupApiVal.postValue(
                            Resource.DataError("Failed to add all vehicles")
                        )
                    } else if (successCount < list.size && list.size > 1) {
                        _addVehiclesToGroupApiVal.postValue(
                            Resource.DataError("Few vehicle(s) failed to add.")
                        )
                    }
                } catch (e: Exception) {
                    _addVehiclesToGroupApiVal.postValue(ResponseHandler.failure(e))
                }
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

    fun getUnAllocatedVehiclesApi() {
        viewModelScope.launch {
            try {
                _unAllocatedVehicleListVal.postValue(
                    ResponseHandler.success(
                        repository.getUnAllocatedVehiclesApiCall(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _unAllocatedVehicleListVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun deleteVehicleApi(deleteVehicleRequest: DeleteVehicleRequest) {
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

    fun downloadVehicleList(type: String?) {
        viewModelScope.launch {
            try {
                _vehicleVRMDownloadVal.postValue(
                    ResponseHandler.success(
                        repository.getDownloadVehicleList(
                            type
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _vehicleVRMDownloadVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateVehicleVRMData(request: VehicleListManagementEditRequest) {
        viewModelScope.launch {
            try {
                _vehicleListManagementEditVal.postValue(
                    ResponseHandler.success(
                        repository.updateVehicleListManagement(
                            request
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _vehicleListManagementEditVal.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun getVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findVehicleMutData.setValue(
                    ResponseHandler.success(
                        repository.getVehicleDetail(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findVehicleMutData.setValue(ResponseHandler.failure(e))
            }
        }
    }

    fun validVehicleCheck(vehicleValidReqModel: ValidVehicleCheckRequest?, agencyId: Int?) {

        viewModelScope.launch {
            try {
                validVehicleMutData.setValue(
                    ResponseHandler.success(
                        repository.validVehicleCheck(
                            vehicleValidReqModel, agencyId
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                validVehicleMutData.setValue(ResponseHandler.failure(e))
            }
        }

    }


}