package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.model.RetrievePaymentListApiResponse
import com.heandroid.model.RetrievePaymentListRequest
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



    fun retrievePaymentListApi(header:String , requestParam: RetrievePaymentListRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = appRepository.retrievePaymentList(header , requestParam)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getMonthlyUsage(header:String , requestParam: RetrievePaymentListRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = appRepository.getMonthlyUsageApiCall(header , requestParam)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}