package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.repo.MainRepository
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers

class DashboardViewModel(private val mainRepository: MainRepository): ViewModel() {

    fun getAccountOverViewApi(authToken:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAccountOverviewApiCall(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getVehicleInformationApi(authToken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getVehicleListInformationApiCall(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}