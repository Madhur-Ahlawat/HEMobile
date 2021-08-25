package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.repo.AppRepository
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val appRepository: AppRepository):ViewModel() {

    fun loginUser(clientID:String,
                  grantType:String,
                  agencyId:String,
                  clientSecret:String,
                  value:String,
                  password:String,
                  validatePasswordCompliance:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = appRepository.loginUser(clientID, grantType, agencyId, clientSecret, value, password, validatePasswordCompliance)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}