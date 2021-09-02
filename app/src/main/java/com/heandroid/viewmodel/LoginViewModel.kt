package com.heandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.heandroid.network.ApiHelper
import com.heandroid.repo.AppRepository
import com.heandroid.repo.Resource
import kotlinx.coroutines.Dispatchers
<<<<<<< HEAD
//
//class LoginViewModel(private val appRepository: AppRepository):ViewModel() {
class LoginViewModel(private val apiHelper: ApiHelper):ViewModel() {
=======

class LoginViewModel(private val appRepository: ApiHelper):ViewModel() {
>>>>>>> 8fce408b1dd05766b77505525f996d347d6ea88d

    fun loginUser(clientID:String,
                  grantType:String,
                  agencyId:String,
                  clientSecret:String,
                  value:String,
                  password:String,
                  validatePasswordCompliance:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
<<<<<<< HEAD
            emit(Resource.success(data = apiHelper.loginApiCall(clientID, grantType, agencyId, clientSecret, value, password, validatePasswordCompliance)))
=======
            emit(Resource.success(data = appRepository.loginApiCall(clientID, grantType, agencyId, clientSecret, value, password, validatePasswordCompliance)))
>>>>>>> 8fce408b1dd05766b77505525f996d347d6ea88d
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getRenewalAccessToken(clientId:String , grantType:String, agencyId:String, clientSecret:String,
                              refreshToken:String, validatePasswordCompliance:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.getRenewalAccessToken(clientId , grantType, agencyId, clientSecret, refreshToken!!, validatePasswordCompliance)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}