package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.repo.AppRepository
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers

class DashboardViewModel(private val appRepository: AppRepository): ViewModel() {

    fun getAccountOverViewApi(authToken:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = appRepository.getAccountOverviewApiCall(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getVehicleInformationApi(authToken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = appRepository.getVehicleListInformationApiCall(authToken)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}